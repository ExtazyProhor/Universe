package ru.prohor.universe.kenobi.app

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import ru.prohor.universe.jocasta.core.collections.common.Opt
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository
import ru.prohor.universe.kenobi.core.KenobiUser
import ru.prohor.universe.kenobi.core.KenobiUserService
import ru.prohor.universe.scarif.jwt.AuthorizedUser
import java.util.UUID

class KenobiUserFilterTest {
    private val repository = MongoInMemoryRepository(KenobiUser::id, KenobiUser::class.java)
    private val filter = KenobiUserFilter(repository, KenobiUserService())
    private val userId = ObjectId()
    private val authorizedUser = AuthorizedUser(1, UUID.randomUUID(), userId.toHexString())

    @Test
    fun `anonymous request does not create or attach Kenobi user`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        filter.doFilter(request, response) { _, _ -> }

        assertEquals(0L, repository.countDocuments())
        assertNull(request.getAttribute(KenobiUser.ATTRIBUTE_KEY))
    }

    @Test
    fun `authorized request creates and attaches Kenobi user`() {
        val request = authorizedRequest()
        val response = MockHttpServletResponse()

        filter.doFilter(request, response) { _, _ -> }

        val user = request.getAttribute(KenobiUser.ATTRIBUTE_KEY) as KenobiUser
        assertEquals(userId, user.id)
        assertNotNull(user.apiToken)
        assertEquals(1L, repository.countDocuments())
    }

    @Test
    fun `existing Kenobi user is reused`() {
        val firstRequest = authorizedRequest()
        filter.doFilter(firstRequest, MockHttpServletResponse()) { _, _ -> }
        val firstUser = firstRequest.getAttribute(KenobiUser.ATTRIBUTE_KEY)

        val secondRequest = authorizedRequest()
        filter.doFilter(secondRequest, MockHttpServletResponse()) { _, _ -> }
        val secondUser = secondRequest.getAttribute(KenobiUser.ATTRIBUTE_KEY)

        assertSame(firstUser, secondUser)
        assertEquals(1L, repository.countDocuments())
    }

    private fun authorizedRequest(): MockHttpServletRequest {
        return MockHttpServletRequest().also {
            it.setAttribute(
                AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY,
                Opt.of(authorizedUser)
            )
        }
    }
}
