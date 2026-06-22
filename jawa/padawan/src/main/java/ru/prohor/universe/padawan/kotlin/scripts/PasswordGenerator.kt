package ru.prohor.universe.padawan.kotlin.scripts

import java.security.SecureRandom

fun main() {

}

class PasswordGenerator {
    private val random = SecureRandom()

    fun generate(
        length: Int = 12,
        useLowercase: Boolean = true,
        useUppercase: Boolean = true,
        useDigits: Boolean = true,
        useSpecial: Boolean = true
    ): String {
        require(length > 0) { "Длина пароля должна быть больше 0" }

        val allowedChars = StringBuilder()
        val mandatoryChars = mutableListOf<Char>()

        if (useLowercase) {
            allowedChars.append(LOWERCASE)
            mandatoryChars.add(LOWERCASE.random(random))
        }
        if (useUppercase) {
            allowedChars.append(UPPERCASE)
            mandatoryChars.add(UPPERCASE.random(random))
        }
        if (useDigits) {
            allowedChars.append(DIGITS)
            mandatoryChars.add(DIGITS.random(random))
        }
        if (useSpecial) {
            allowedChars.append(SPECIAL_CHARS)
            mandatoryChars.add(SPECIAL_CHARS.random(random))
        }

        require(length >= mandatoryChars.size) {
            "Длина пароля слишком мала для выбранных параметров (минимум: ${mandatoryChars.size})"
        }

        val password = StringBuilder()
        password.append(mandatoryChars.joinToString(""))

        val remainingLength = length - mandatoryChars.size
        repeat(remainingLength) {
            val randomIndex = random.nextInt(allowedChars.length)
            password.append(allowedChars[randomIndex])
        }

        return password.toString().toList().shuffled(random).joinToString("")
    }

    private fun String.random(secureRandom: SecureRandom): Char {
        return this[secureRandom.nextInt(this.length)]
    }

    private companion object {
        private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
        private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val DIGITS = "0123456789"
        private const val SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?"
    }
}
