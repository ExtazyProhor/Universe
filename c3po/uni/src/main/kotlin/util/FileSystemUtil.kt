package ru.prohor.universe.uni.cli.util

import java.io.File

fun isCommonFile(file: File) = file.isFile && !file.name.startsWith(".")
