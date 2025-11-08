package ru.prohor.universe.venator.webhook

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class IpUtilsTest {
    @Test
    fun testToBitRepresentation1() {
        val ip = "192.168.1.222"
        val bits = ip.toBitRepresentation()
        val expected = "11000000 10101000 00000001 11011110".replace(" ", "")
        assertEquals(expected, bits)
    }

    @Test
    fun testToBitRepresentation2() {
        val ip = "0.1.2.3"
        val bits = ip.toBitRepresentation()
        val expected = "00000000 00000001 00000010 00000011".replace(" ", "")
        assertEquals(expected, bits)
    }

    @Test
    fun testToBitRepresentation3() {
        val ip = "254.253.252.251"
        val bits = ip.toBitRepresentation()
        val expected = "11111110 11111101 11111100 11111011".replace(" ", "")
        assertEquals(expected, bits)
    }

    @Test
    fun testToBitRepresentationMaxIp() {
        val ip = "255.255.255.255"
        val bits = ip.toBitRepresentation()
        assertEquals("1".repeat(32), bits)
    }

    @Test
    fun testToBitRepresentationMinIp() {
        val ip = "0.0.0.0"
        val bits = ip.toBitRepresentation()
        assertEquals("0".repeat(32), bits)
    }

    @Test
    fun testToBitRepresentationThrowsExceptionWhenIllegalOctet1() {
        val ip = "999.1.1.1"
        val exception = assertThrows<IllegalArgumentException> {
            ip.toBitRepresentation()
        }

        assertNotNull(exception)
        assertNotNull(exception.message)
        assertTrue(exception.message?.contains("Illegal ip octet format: 999") ?: true)
    }

    @ParameterizedTest
    @ValueSource(strings = ["256", "257", "258", "999"])
    fun testToBitRepresentationThrowsExceptionWhenIllegalOctet2(octet: String) {
        val ip = "$octet.0.0.1"
        val exception = assertThrows<IllegalArgumentException> {
            ip.toBitRepresentation()
        }

        assertNotNull(exception)
        assertNotNull(exception.message)
        assertTrue(exception.message?.contains("Illegal ip octet format: $octet") ?: true)
    }

    @Test
    fun testToBitPrefixWithMaskReturnsCorrectPrefixForValidCIDR() {
        assertEquals("11000000101010000000000100000001", "192.168.1.1/32".toBitPrefixWithMask())
        assertEquals("1100000010101000000000010000000", "192.168.1.1/31".toBitPrefixWithMask())
        assertEquals("110000001010100000000001000000", "192.168.1.1/30".toBitPrefixWithMask())
        assertEquals("11000000101010000000000100000", "192.168.1.1/29".toBitPrefixWithMask())
        assertEquals("110000001010100000000001", "192.168.1.1/24".toBitPrefixWithMask())
        assertEquals("1100000010101000", "192.168.1.1/16".toBitPrefixWithMask())
    }

    @Test
    fun testToBitPrefixWithMaskHandlesMinMaskValue() {
        assertEquals("1100000010101000", "192.168.1.1/16".toBitPrefixWithMask())
    }

    @Test
    fun testToBitPrefixWithMaskHandlesMaxMaskValue() {
        assertEquals("11000000101010000000000100000001", "192.168.1.1/32".toBitPrefixWithMask())
    }

    @Test
    fun testToBitPrefixWithMaskThrowsExceptionForMaskThatLessThan16() {
        val ex = assertThrows<IllegalArgumentException> {
            "192.168.1.1/15".toBitPrefixWithMask()
        }
        assertEquals("Illegal network mask format: 15", ex.message)
    }

    @Test
    fun testToBitPrefixWithMaskThrowsExceptionForMaskGreaterThan32() {
        val ex = assertThrows<IllegalArgumentException> {
            "192.168.1.1/33".toBitPrefixWithMask()
        }
        assertEquals("Illegal network mask format: 33", ex.message)
    }

    @Test
    fun testToBitPrefixWithMaskThrowsExceptionForMaskEquals0() {
        val ex = assertThrows<IllegalArgumentException> {
            "192.168.1.1/0".toBitPrefixWithMask()
        }
        assertEquals("Illegal network mask format: 0", ex.message)
    }

    @Test
    fun testToBitPrefixWithMaskThrowsExceptionForInvalidIPInCIDR() {
        val ex = assertThrows<IllegalArgumentException> {
            "256.168.1.1/24".toBitPrefixWithMask()
        }
        assertEquals("Illegal ip octet format: 256", ex.message)
    }

    @Test
    fun testToBitPrefixWithMaskWorksWithDifferentIPAddresses() {
        assertEquals("00001010000000000000000000000001", "10.0.0.1/32".toBitPrefixWithMask())
        assertEquals("000010100000000000000000", "10.0.0.1/24".toBitPrefixWithMask())
        assertEquals("11111111111111111111111111111111", "255.255.255.255/32".toBitPrefixWithMask())
        assertEquals("0000000000000000", "0.0.0.0/16".toBitPrefixWithMask())
    }
}
