package ru.prohor.universe.venator.webhook

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class IpValidationService(
    // https://api.github.com/meta
    @Value($$"${universe.venator.webhook.permitted-ip}") permittedIps: List<String>
) {
    private val bitPrefixes = permittedIps.map { permittedIp ->
        if (!permittedIp.matches(IP_MASK_PATTERN))
            throw IllegalArgumentException("Illegal format of IP address: $permittedIp. Use x.x.x.x/m")
        permittedIp.toBitPrefixWithMask()
    }

    fun isValidIp(ip: String): Boolean {
        if (!ip.matches(IP_PATTERN))
            throw IllegalArgumentException("Illegal format of IP address: $ip. Must be like x.x.x.x")
        val bitPrefix = ip.toBitRepresentation()
        return bitPrefixes.any { bitPrefix.startsWith(it) }
    }

    companion object {
        private val IP_PATTERN = Regex("^(\\d{1,3}\\.){3}\\d{1,3}$")
        private val IP_MASK_PATTERN = Regex("^(\\d{1,3}\\.){3}\\d{1,3}/\\d{2}$")
    }
}
