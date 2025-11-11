package ru.prohor.universe.venator.webhook

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.test.context.TestConstructor
import ru.prohor.universe.venator.Venator
import ru.prohor.universe.venator.webhook.model.ApiResponse
import ru.prohor.universe.venator.webhook.model.WebhookPayload

@SpringBootTest(
    classes = [Venator::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class WebhookControllerTest(
    @param:Value($$"${universe.venator.webhook.secret}") private val secret: String?,
    private val rest: TestRestTemplate
) {
    @BeforeEach
    fun setupDebugBeans() {
        DebugWebhookAction.payload = null
        DebugWebhookNotifier.wasFailure = false
        DebugWebhookNotifier.wasInfo = false
        DebugWebhookNotifier.wasSuccess = false
    }

    @Test
    fun testParseWebhookPayload() {
        val payload = getResource("/webhook-payload-mock.json")
        val headers = HttpHeaders()
        addSignature(payload, headers)

        val response = doRequest(payload, headers)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertTrue(DebugWebhookNotifier.wasSuccess)

        val expectedRestPayload = getResource("/webhook-rest-payload-mock.json")
        val parsedPayload = DebugWebhookAction.payload
        Assertions.assertNotNull(parsedPayload)

        JSONAssert.assertEquals(
            expectedRestPayload,
            ObjectMapper().writeValueAsString(parsedPayload),
            JSONCompareMode.NON_EXTENSIBLE
        )
    }

    @Test
    fun testWebhookWithoutSignature() {
        val payload = getResource("/webhook-payload-mock.json")
        val headers = HttpHeaders()

        val response = doRequest(payload, headers)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        Assertions.assertTrue(DebugWebhookNotifier.wasFailure)
    }

    @Test
    fun testWebhookWithIllegalSignature() {
        val payload = getResource("/webhook-payload-mock.json")
        val headers = HttpHeaders()
        addSignature(payload, headers, "some_illegal_secret")

        val response = doRequest(payload, headers)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        Assertions.assertTrue(DebugWebhookNotifier.wasFailure)
    }

    @Test
    fun testWebhookWithIllegalIp() {
        val payload = getResource("/webhook-payload-mock.json")
        val headers = HttpHeaders()
        addSignature(payload, headers)
        headers.add("X-Real-IP", "123.123.123.123")

        val response = doRequest(payload, headers)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        Assertions.assertTrue(DebugWebhookNotifier.wasFailure)
    }

    @Test
    fun testWebhookWithAnotherRepository() {
        val payload = getResource("/webhook-payload-mock.json")
            .replace(
                "\"full_name\": \"ExtazyProhor/Universe\"",
                "\"full_name\": \"ExtazyProhor/Universe2\""
            )
        val headers = HttpHeaders()
        addSignature(payload, headers)

        val response = doRequest(payload, headers)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertTrue(DebugWebhookNotifier.wasFailure)
        Assertions.assertNotNull(response.body)
        JSONAssert.assertEquals(
            ObjectMapper().writeValueAsString(ApiResponse.error("Unknown repository")),
            response.body,
            JSONCompareMode.NON_EXTENSIBLE
        )
    }

    @Test
    fun testWebhookWithAnotherBranch() {
        val payload = getResource("/webhook-payload-mock.json")
            .replace(
                "\"ref\": \"refs/heads/main\"",
                "\"ref\": \"refs/heads/some-branch\""
            )
        val headers = HttpHeaders()
        addSignature(payload, headers)

        val response = doRequest(payload, headers)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertTrue(DebugWebhookNotifier.wasInfo)
        Assertions.assertNotNull(response.body)
        JSONAssert.assertEquals(
            ObjectMapper().writeValueAsString(ApiResponse.success("Webhook ignored, wrong branch: some-branch")),
            response.body,
            JSONCompareMode.NON_EXTENSIBLE
        )
    }

    @Test
    fun testWebhookWithNullFields() {
        val payload = getResource("/webhook-payload-mock.json")
            .replace("\"full_name\": \"ExtazyProhor/Universe\",", "")
            .replace("\"ref\": \"refs/heads/main\",", "\"ref\": null,")
        val headers = HttpHeaders()
        addSignature(payload, headers)

        val response = doRequest(payload, headers)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertTrue(DebugWebhookNotifier.wasFailure)
        JSONAssert.assertEquals(
            ObjectMapper().writeValueAsString(ApiResponse.error("Illegal body structure")),
            response.body,
            JSONCompareMode.NON_EXTENSIBLE
        )
    }

    private fun addSignature(payload: String, headers: HttpHeaders, secret: String? = null) {
        val hmacUtils = HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret ?: this.secret)
        headers.add("X-Hub-Signature-256", "sha256=" + hmacUtils.hmacHex(payload))
    }

    private fun doRequest(payload: String, headers: HttpHeaders): ResponseEntity<String> {
        headers.contentType = MediaType.APPLICATION_JSON
        return rest.exchange(
            "/webhook/on_push",
            HttpMethod.POST,
            HttpEntity(payload, headers),
            String::class.java
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

    @Service
    @Primary
    class DebugWebhookNotifier : WebhookNotifier {
        override fun failure(message: String) {
            wasFailure = true
        }

        override fun info(message: String) {
            wasInfo = true
        }

        override fun success(payload: WebhookPayload) {
            wasSuccess = true
        }

        companion object {
            var wasFailure = false
            var wasInfo = false
            var wasSuccess = false
        }
    }
}
