package ru.prohor.universe.jocasta.core.collections.common

import java.util.Optional

fun <T> Optional<T>.toOpt(): Opt<T> = Opt.wrap(this)

fun <T> Optional<T>.toNullable(): T? = orElse(null)
