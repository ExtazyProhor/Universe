package ru.prohor.universe.venator.webhook.service

private const val IP_BITS = 32
private const val OCTET_BITS = 8
private const val MIN_MASK = 16
private const val MIN_OCTET_VALUE = 0
private const val MAX_OCTET_VALUE = 255

/**
 * Must be called on string, like "255.255.255.255/24". Pattern:
 * ```regexp
 * ^(\d{1,3}\.){3}\d{1,3}/\d{2}$
 * ```
 */
fun String.toBitPrefixWithMask(): String {
    val (ip, mask) = split("/").let { it[0] to it[1].toInt() }
    if (mask !in MIN_MASK..IP_BITS)
        throw IllegalArgumentException("Illegal network mask format: $mask")
    return ip.toBitRepresentation().dropLast(IP_BITS - mask)
}

/**
 * Must be called on string, like "255.255.255.255". Pattern:
 * ```regexp
 * ^(\d{1,3}\.){3}\d{1,3}$
 * ```
 */
fun String.toBitRepresentation() = split(Regex("\\.")).map { it.toInt() }.joinToString(separator = "") { octet ->
    if (octet !in MIN_OCTET_VALUE..MAX_OCTET_VALUE)
        throw IllegalArgumentException("Illegal ip octet format: $octet")
    octet.toString(2).let { "0".repeat(OCTET_BITS - it.length) + it }
}
