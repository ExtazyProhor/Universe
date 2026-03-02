package ru.prohor.universe.bb8

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

@RestController
@RequestMapping("/api/files")
class FilesExchangerController(
    @Value($$"${universe.bb8.upload-directory}")
    uploadDirectory: String,
    @param:Value($$"${universe.bb8.password}")
    private val password: String
) {
    private val uploadDirectory = Path(uploadDirectory).toAbsolutePath().normalize()
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

    init {
        Files.createDirectories(this.uploadDirectory)
    }

    @GetMapping
    fun listFiles(): ResponseEntity<List<FileInfo>> {
        val files = Files.list(uploadDirectory)
            .filter { Files.isRegularFile(it) }
            .map { path ->
                val attrs = Files.readAttributes(path, java.nio.file.attribute.BasicFileAttributes::class.java)
                FileInfo(
                    name = path.fileName.toString(),
                    size = attrs.size(),
                    sizeFormatted = formatSize(attrs.size()),
                    lastModified = formatter.format(Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis()))
                )
            }
            .sorted(Comparator.comparing { it.name })
            .toList()
        return ResponseEntity.ok(files)
    }

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("password") password: String
    ): ResponseEntity<ApiResponse> {
        if (password != this.password) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse(false, "Неверный пароль"))
        }

        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(ApiResponse(false, "Файл пустой"))
        }

        val filename = sanitizeFilename(file.originalFilename ?: "file")
        val targetPath = uploadDirectory.resolve(filename).normalize()

        if (!targetPath.startsWith(uploadDirectory)) {
            return ResponseEntity.badRequest().body(ApiResponse(false, "Недопустимое имя файла"))
        }

        try {
            Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: IOException) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse(false, "Ошибка при сохранении файла: ${e.message}"))
        }
        return ResponseEntity.ok(ApiResponse(true, "Файл «$filename» успешно загружен"))
    }

    @GetMapping("/download/{filename}")
    fun downloadFile(@PathVariable("filename") filename: String): ResponseEntity<Resource> {
        val filePath = uploadDirectory.resolve(filename).normalize()

        if (!filePath.startsWith(uploadDirectory)) {
            return ResponseEntity.badRequest().build()
        }

        val resource: Resource = try {
            UrlResource(filePath.toUri())
        } catch (_: MalformedURLException) {
            return ResponseEntity.badRequest().build()
        }

        if (!resource.exists() || !resource.isReadable) {
            return ResponseEntity.notFound().build()
        }

        val contentType = try {
            Files.probeContentType(filePath) ?: "application/octet-stream"
        } catch (_: IOException) {
            "application/octet-stream"
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${resource.filename}\"")
            .body(resource)
    }

    private fun sanitizeFilename(filename: String): String {
        return filename.replace(Regex("[^a-zA-Zа-яА-ЯёЁ0-9._\\- ]"), "_")
            .trimStart('.')
            .ifEmpty { "file" }
    }

    private fun formatSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes Б"
        bytes < 1024 * 1024 -> "%.1f КБ".format(bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> "%.1f МБ".format(bytes / (1024.0 * 1024))
        else -> "%.1f ГБ".format(bytes / (1024.0 * 1024 * 1024))
    }

    data class FileInfo(
        val name: String,
        val size: Long,
        val sizeFormatted: String,
        val lastModified: String
    )

    data class ApiResponse(
        val success: Boolean,
        val message: String
    )
}
