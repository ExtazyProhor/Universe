package ru.prohor.universe.venator.webhook

import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SignatureService(
    @Value($$"${universe.venator.webhook.secret}") secret: String
) {
    private val hmacUtils: HmacUtils = HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret)

    fun verifySignature(signature: String?, body: String): Boolean {
        return signature?.let { it == "sha256=${hmacUtils.hmacHex(body)}" } ?: false
    }
}
