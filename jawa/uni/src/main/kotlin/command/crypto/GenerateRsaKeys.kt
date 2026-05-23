package ru.prohor.universe.uni.cli.command.crypto

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.TextColors.yellow
import ru.prohor.universe.uni.cli.command.UniCommand
import java.security.KeyPairGenerator
import java.util.Base64

class GenerateRsaKeys : UniCommand(name = "rsa") {
    private val bits by option("-b", "--bits", help = "key size in bits").int().default(2048)

    override fun help(context: Context) = "generates an RSA key pair"

    override fun run() {
        val keyPairGen = KeyPairGenerator.getInstance("RSA")
        keyPairGen.initialize(bits)

        val keyPair = keyPairGen.generateKeyPair()
        val encoder = Base64.getEncoder()

        val privateKeyRaw = encoder.encodeToString(keyPair.private.encoded)
        println(yellow("=== Private Key ==="))
        println(privateKeyRaw)
        println()

        val publicKeyRaw = encoder.encodeToString(keyPair.public.encoded)
        println(yellow("=== Public Key ==="))
        println(publicKeyRaw)
    }
}
