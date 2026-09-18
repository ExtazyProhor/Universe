package ru.prohor.universe.fondor.core

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SPAController(@param:Value($$"${universe.fondor.root-path}") private val rootPath: String) {
    @GetMapping("/**")
    fun handleAllRequests(request: HttpServletRequest): ResponseEntity<FileSystemResource> {
        val uri = request.requestURI
        if (uri.startsWith("/error") || uri.startsWith("/actuator")) {
            return ResponseEntity.notFound().build()
        }

        val relativePath = uri.removePrefix("/")
        val requestedResource = FileSystemResource("$rootPath/$relativePath")
        if (requestedResource.exists() && requestedResource.isReadable) {
            val mediaType = MediaTypeFactory.getMediaType(requestedResource).orElse(null)
                ?: MediaType.APPLICATION_OCTET_STREAM
            return ResponseEntity.ok().contentType(mediaType).body(requestedResource)
        }

        val indexHtml = FileSystemResource("${rootPath}/index.html")
        if (indexHtml.exists() && indexHtml.isReadable) {
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(indexHtml)
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
}
