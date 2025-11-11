package ru.prohor.universe.venator.webhook

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.prohor.universe.venator.webhook.service.IpValidationService

class IpValidationServiceTest {
    @Test
    fun testIsValidIpReturnsTrueWhenIpMatchesPermittedRange() {
        val service = IpValidationService(listOf("192.168.1.0/24"))

        assertTrue(service.isValidIp("192.168.1.0"))
        assertTrue(service.isValidIp("192.168.1.1"))
        assertTrue(service.isValidIp("192.168.1.254"))
        assertTrue(service.isValidIp("192.168.1.255"))
    }

    @Test
    fun testIsValidIpReturnsFalseWhenIpDoesNotMatchPermittedRange() {
        val service = IpValidationService(listOf("192.168.1.0/24"))

        assertFalse(service.isValidIp("192.168.2.1"))
        assertFalse(service.isValidIp("10.0.0.1"))
        assertFalse(service.isValidIp("172.16.0.1"))
    }

    @Test
    fun testIsValidIpWorksWithMultiplePermittedRanges() {
        val service = IpValidationService(listOf("192.168.1.0/24", "10.0.0.0/16"))

        assertTrue(service.isValidIp("192.168.1.1"))
        assertTrue(service.isValidIp("10.0.5.10"))
        assertFalse(service.isValidIp("172.16.0.1"))
    }

    @Test
    fun testIsValidIpThrowsExceptionForInvalidIpFormat() {
        val service = IpValidationService(listOf("192.168.1.0/24"))

        val exception1 = assertThrows<IllegalArgumentException> {
            service.isValidIp("192.168.1")
        }
        assertEquals("Illegal format of IP address: 192.168.1. Must be like x.x.x.x", exception1.message)

        val exception2 = assertThrows<IllegalArgumentException> {
            service.isValidIp("192.168.1.1.1")
        }
        assertEquals("Illegal format of IP address: 192.168.1.1.1. Must be like x.x.x.x", exception2.message)

        val exception3 = assertThrows<IllegalArgumentException> {
            service.isValidIp("invalid")
        }
        assertEquals("Illegal format of IP address: invalid. Must be like x.x.x.x", exception3.message)
    }

    @Test
    fun testConstructorThrowsExceptionForInvalidPermittedIpFormat() {
        val exception1 = assertThrows<IllegalArgumentException> {
            IpValidationService(listOf("192.168.1.0"))
        }
        assertEquals("Illegal format of IP address: 192.168.1.0. Use x.x.x.x/m", exception1.message)

        val exception2 = assertThrows<IllegalArgumentException> {
            IpValidationService(listOf("192.168.1.0/"))
        }
        assertEquals("Illegal format of IP address: 192.168.1.0/. Use x.x.x.x/m", exception2.message)

        val exception3 = assertThrows<IllegalArgumentException> {
            IpValidationService(listOf("192.168.1.0/8"))
        }
        assertEquals("Illegal format of IP address: 192.168.1.0/8. Use x.x.x.x/m", exception3.message)
    }

    @Test
    fun testIsValidIpWorksWithStrictMask() {
        val service = IpValidationService(listOf("192.168.1.100/32"))

        assertTrue(service.isValidIp("192.168.1.100"))
        assertFalse(service.isValidIp("192.168.1.101"))
        assertFalse(service.isValidIp("192.168.1.99"))
    }

    @Test
    fun testIsValidIpWorksWithBroadMask() {
        val service = IpValidationService(listOf("10.0.0.0/16"))

        assertTrue(service.isValidIp("10.0.0.1"))
        assertTrue(service.isValidIp("10.0.255.255"))
        assertFalse(service.isValidIp("10.1.0.1"))
    }

    @Test
    fun testIsValidIpHandlesEdgeCaseIpAddresses() {
        val service = IpValidationService(listOf("0.0.0.0/24"))

        assertTrue(service.isValidIp("0.0.0.0"))
        assertTrue(service.isValidIp("0.0.0.255"))
    }

    @Test
    fun testIsValidIpWithEmptyPermittedList() {
        val service = IpValidationService(emptyList())

        assertFalse(service.isValidIp("192.168.1.1"))
        assertFalse(service.isValidIp("10.0.0.1"))
    }
}
