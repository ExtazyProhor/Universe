package ru.prohor.universe.kenobi.app

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.prohor.universe.jocasta.core.collections.common.Opt
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.kenobi.core.KenobiUser
import ru.prohor.universe.kenobi.core.KenobiUserService
import ru.prohor.universe.scarif.jwt.AuthorizedUser

@Component
@Order(-90)
class KenobiUserFilter(
    private val usersRepository: MongoRepository<KenobiUser>,
    private val userService: KenobiUserService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val kenobiUser = getAuthorizedUser(request)?.let { authorizedUser ->
                val userId = ObjectId(authorizedUser.objectId)
                usersRepository.withTransactionResult { repository ->
                    repository.findById(userId).orElseGet {
                        userService.createUser(id = userId).also(repository::save)
                    }
                }
            }
            request.setAttribute(KenobiUser.ATTRIBUTE_KEY, kenobiUser)
            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            log.error("KenobiUserFilter error", exception)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getAuthorizedUser(request: HttpServletRequest): AuthorizedUser? {
        val attribute = request.getAttribute(AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY) as? Opt<AuthorizedUser>
        return attribute?.orElseNull()
    }

    companion object {
        private val log = LoggerFactory.getLogger(KenobiUserFilter::class.java)
    }
}
