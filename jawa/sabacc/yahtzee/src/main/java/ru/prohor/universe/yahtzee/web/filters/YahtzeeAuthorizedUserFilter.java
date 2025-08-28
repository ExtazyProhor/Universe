package ru.prohor.universe.yahtzee.web.filters;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.scarifJwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.services.UserService;
import ru.prohor.universe.yahtzee.web.YahtzeeAuthorizedUser;

import java.io.IOException;

public class YahtzeeAuthorizedUserFilter extends OncePerRequestFilter {
    public static final int YAHTZEE_AUTHORIZED_USER_FILTER_ORDER = 10;

    private final UserService userService;

    public YahtzeeAuthorizedUserFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        Opt<AuthorizedUser> authorizedUser = Opt.ofNullable(
                request.getAttribute(
                        AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY
                )
        ).cast();

        request.setAttribute(
                YahtzeeAuthorizedUser.ATTRIBUTE_KEY,
                authorizedUser.map(userService::wrap)
        );
        filterChain.doFilter(request, response);
    }
}
