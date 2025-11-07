package ru.prohor.universe.venator.webhook

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.test.context.TestConstructor
import ru.prohor.universe.venator.Venator
import ru.prohor.universe.venator.webhook.model.WebhookPayload

@SpringBootTest(
    classes = [Venator::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(WebhookControllerTest.DebugWebhookAction::class)
class WebhookControllerTest(
    @param:Value($$"${universe.venator.webhook.secret}") private val secret: String?,
    private val rest: TestRestTemplate
) {
    @Test
    fun testParseWebhookPayload() {
        val payload = getResource("/webhook-payload-mock.json")
        val headers = HttpHeaders()
        val hmacUtils = HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret)
        headers.add("X-Hub-Signature-256", "sha256=" + hmacUtils.hmacHex(payload))
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)

        val response = rest.exchange(
            "/webhook/on_push",
            HttpMethod.POST,
            HttpEntity(payload, headers),
            String::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val expectedRestPayload = getResource("/webhook-rest-payload-mock.json")
        val parsedPayload = DebugWebhookAction.Companion.payload
        Assertions.assertNotNull(parsedPayload)

        JSONAssert.assertEquals(
            expectedRestPayload,
            ObjectMapper().writeValueAsString(parsedPayload),
            JSONCompareMode.NON_EXTENSIBLE
        )
    }

    private fun getResource(location: String): String {
        val url = WebhookControllerTest::class.java.getResource(location)
        if (url == null)
            throw IllegalArgumentException("url $url is null")
        try {
            url.openStream().use { stream -> return String(stream.readAllBytes()) }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Service
    @Primary
    class DebugWebhookAction : WebhookAction {
        override fun accept(payload: WebhookPayload) {
            DebugWebhookAction.payload = payload
        }

        companion object {
            var payload: WebhookPayload? = null
        }
    }
}
