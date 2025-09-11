package ru.prohor.universe.scarif.services;

import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.scarif.data.user.User;

import java.util.List;

@Service
public class RegistrationService {
    private final ValidationService validationService;
    private final PasswordService passwordService;
    private final UserService userService;

    public RegistrationService(
            ValidationService validationService,
            PasswordService passwordService,
            UserService userService
    ) {
        this.validationService = validationService;
        this.passwordService = passwordService;
        this.userService = userService;
    }

    public User registerUser(
            String username,
            String email,
            String password
    ) throws RegistrationException {
        Opt<String> err = validationService.validateUsername(username)
                .orElse(validationService.validateEmail(email))
                .orElse(validationService.validatePassword(password, List.of(username, email)));
        if (err.isPresent())
            throw new RegistrationException(new RegistrationClientError(err.get()));

        if (userService.existsByEmail(email))
            throw new RegistrationException(new RegistrationNonUniqueEmailError(true));
        if (userService.existsByUsername(username))
            throw new RegistrationException(new RegistrationNonUniqueUsernameError(true));

        User user = userService.createUser(
                username,
                email,
                passwordService.hash(password)
        );
        userService.register(user);
        return user;
    }

    public static class RegistrationException extends Exception {
        public final RegistrationError cause;

        public RegistrationException(RegistrationError cause) {
            this.cause = cause;
        }
    }

    public interface RegistrationError {}

    public record RegistrationNonUniqueEmailError(boolean emailInNotUnique) implements RegistrationError {}

    public record RegistrationNonUniqueUsernameError(boolean usernameInNotUnique) implements RegistrationError {}

    public record RegistrationClientError(String errorMessage) implements RegistrationError {}
}
