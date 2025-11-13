package ru.prohor.universe.venator.fs

import java.util.concurrent.TimeUnit

data class Timeout(
    val timeout: Long,
    val unit: TimeUnit
)
