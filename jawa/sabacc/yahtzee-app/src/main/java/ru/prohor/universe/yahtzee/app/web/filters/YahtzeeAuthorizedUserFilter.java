package ru.prohor.universe.yahtzee.app.web.filters;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.app.services.AccountService;

import java.io.IOException;

public class YahtzeeAuthorizedUserFilter extends OncePerRequestFilter {
    public static final int YAHTZEE_AUTHORIZED_USER_FILTER_ORDER = 10;

    private final AccountService accountService;

    public YahtzeeAuthorizedUserFilter(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        @SuppressWarnings("unchecked")
        Opt<AuthorizedUser> authorizedUser = (Opt<AuthorizedUser>) request.getAttribute(
                AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY
        );
        request.setAttribute(
                Player.ATTRIBUTE_KEY,
                authorizedUser.map(accountService::wrap)
        );
        filterChain.doFilter(request, response);
    }
}
