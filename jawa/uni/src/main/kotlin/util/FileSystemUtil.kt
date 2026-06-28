package ru.prohor.universe.uni.cli.util

import java.io.File
import java.security.MessageDigest

fun isCommonFile(file: File) = file.isFile && !file.name.startsWith(".")

fun fileHash(file: File): String {
    val messageDigest = MessageDigest.getInstance("MD5")
    file.inputStream().use { buffer ->
        val bytes = ByteArray(8192)
        var read = buffer.read(bytes)
        while (read != -1) {
            messageDigest.update(bytes, 0, read)
            read = buffer.read(bytes)
        }
    }
    return messageDigest.digest().joinToString("") { "%02x".format(it) }
}
