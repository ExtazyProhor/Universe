package ru.prohor.universe.venator.webhook

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import ru.prohor.universe.venator.Venator

@ActiveProfiles("testing")
@SpringBootTest(classes = [Venator::class])
@TestPropertySource(value = ["classpath:application-stable.properties", "classpath:application.properties"])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class GithubWebhookIpsValidationTest(
    @param:Value($$"${universe.venator.webhook.permitted-ip}") private val permittedIps: List<String>,
    @param:Value($$"${universe.venator.webhook.github-meta-url}") private val githubMetaUrl: String
) {
    @Test
    fun validateIps() {
        val restTemplate = RestTemplate()
        val objectMapper = ObjectMapper()

        val json = restTemplate.getForObject(githubMetaUrl, String::class.java)
        val root: JsonNode = objectMapper.readTree(json)
        val ipsArray: JsonNode? = root.get("hooks")

        val ips = ipsArray
            ?.takeIf { it.isArray }
            ?.mapNotNull { it.asText() }
            ?.filter { !it.contains("::") } // not ipv6
            ?.toSet()
            ?: throw RuntimeException("Field 'hooks' not array or missing")

        val permittedIps = permittedIps.toSet()
        Assertions.assertEquals(permittedIps, ips)
    }
}
