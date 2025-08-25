package ru.prohor.universe.scarif.services;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.scarif.data.user.User;

import java.util.List;
import java.util.function.Function;

@Service
public class LoginService {
    private final ValidationService validationService;
    private final PasswordService passwordService;
    private final UserService userService;

    public LoginService(
            ValidationService validationService,
            PasswordService passwordService,
            UserService userService
    ) {
        this.validationService = validationService;
        this.passwordService = passwordService;
        this.userService = userService;
    }

    public User authenticateUser(
            String login,
            String password
    ) throws LoginException {
        boolean isLoginEmail = login.contains("@");
        Function<String, Opt<String>> testLogin = isLoginEmail ?
                validationService::validateEmail :
                validationService::validateUsername;

        Opt<String> err = validationService.validatePassword(password, List.of(login)).orElse(testLogin.apply(login));
        if (err.isPresent())
            throw new LoginException(new LoginClientError(err.get()), HttpServletResponse.SC_BAD_REQUEST);

        Opt<User> user = isLoginEmail ? userService.findByEmail(login) : userService.findByUsername(login);
        if (user.isEmpty() || !passwordService.matches(password, user.get().password()))
            throw new LoginException(new LoginInvalidLoginOrPasswordError(true), HttpServletResponse.SC_UNAUTHORIZED);

        return user.get();
    }

    public static class LoginException extends Exception {
        public final LoginError cause;
        public final int code;

        public LoginException(LoginError cause, int code) {
            this.cause = cause;
            this.code = code;
        }
    }

    public interface LoginError {}

    public record LoginInvalidLoginOrPasswordError(boolean invalidLoginOrPassword) implements LoginError {}

    public record LoginClientError(String errorMessage) implements LoginError {}
}
