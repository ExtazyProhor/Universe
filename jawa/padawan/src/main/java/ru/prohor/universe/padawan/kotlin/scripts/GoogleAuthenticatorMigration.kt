package ru.prohor.universe.padawan.kotlin.scripts

import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.HashingAlgorithm
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import ru.prohor.universe.padawan.proto.generated.MigrationPayload
import java.io.File
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

private const val TIME_STEP_SECONDS = 30L
private val BASE_32_CODEC = Base32()
private val CODE_GENERATOR_SIX = DefaultCodeGenerator(HashingAlgorithm.SHA1, 6)
private val CODE_GENERATOR_EIGHT = DefaultCodeGenerator(HashingAlgorithm.SHA1, 8)

data class TotpResult(
    val code: String,
    val secondsLeft: Long
)

data class Account(
    val name: String,
    val issuer: String?,
    val secret: String,
    val generator: DefaultCodeGenerator
)

fun main() {
    val uri = getMigrationUri()
    parseGoogleAuthenticatorMigration(uri).forEach { processAccount(it) }
}

fun getMigrationUri(): String {
    return JSONObject(File(System.getenv("HOLOCRON_JSON")).readText()).getString("my.totp.migration-uri")
}

fun parseGoogleAuthenticatorMigration(migrationUri: String): List<Account> {
    val uri = URI(migrationUri)
    require(uri.scheme == "otpauth-migration") { "Invalid scheme: ${uri.scheme}" }
    val query = uri.rawQuery ?: throw IllegalArgumentException("Missing query")
    val data = query
        .split("&")
        .firstOrNull { it.startsWith("data=") }
        ?.substringAfter("=")
        ?: throw IllegalArgumentException("Missing data parameter")

    val base64 = URLDecoder.decode(data, StandardCharsets.UTF_8)
    val protobuf = Base64.decodeBase64(base64)
    val payload = MigrationPayload.parseFrom(protobuf)

    return payload.otpParametersList.map { otp ->
        if (otp.algorithm != MigrationPayload.Algorithm.SHA1) {
            throw IllegalArgumentException("Illegal algorithm: ${otp.algorithm}")
        }
        if (otp.type != MigrationPayload.OtpType.TOTP) {
            throw IllegalArgumentException("Illegal OTP type: ${otp.type}")
        }
        Account(
            name = otp.name,
            issuer = otp.issuer.ifEmpty { null },
            secret = BASE_32_CODEC.encodeAsString(otp.secret.toByteArray()),
            generator = when (otp.digits) {
                MigrationPayload.DigitCount.SIX -> CODE_GENERATOR_SIX
                MigrationPayload.DigitCount.EIGHT -> CODE_GENERATOR_EIGHT
                else -> throw IllegalArgumentException("Illegal digit count: ${otp.digits}")
            }
        )
    }
}

fun generateTOTPWithTime(account: Account): TotpResult {
    val currentTimeSeconds = System.currentTimeMillis() / 1000
    val secondsLeft = TIME_STEP_SECONDS - (currentTimeSeconds % TIME_STEP_SECONDS)
    val currentBucket = currentTimeSeconds / TIME_STEP_SECONDS
    val code = account.generator.generate(account.secret.uppercase(), currentBucket)
    return TotpResult(code, secondsLeft)
}

fun processAccount(account: Account) {
    account.issuer?.let { println("Issuer: $it") }
    println("Account: ${account.name}")
    println("Secret: ${account.secret}")
    val totp = generateTOTPWithTime(account)
    println()
    println("Code: ${totp.code}")
    println("Seconds left: ${totp.secondsLeft}")
    println("-".repeat(50))
}
