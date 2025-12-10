package ru.prohor.universe.scarif.services;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    private static final int MIN_PWD_LEN = 8;
    private static final int MAX_PWD_LEN = 64;

    private final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();
    private final Predicate<String> USERNAME_MATCHER = Pattern.compile("[a-zA-Z][a-zA-Z0-9]{2,15}").asMatchPredicate();
    private final List<Tuple2<Predicate<String>, String>> PASSWORD_MATCHERS;

    public ValidationService() {
        PASSWORD_MATCHERS = new ArrayList<>(List.of(
                new Tuple2<>(Pattern.compile(".*[A-Z].*").asMatchPredicate(), "uppercase letter"),
                new Tuple2<>(Pattern.compile(".*[a-z].*").asMatchPredicate(), "lowercase letter"),
                new Tuple2<>(Pattern.compile(".*\\d.*").asMatchPredicate(), "digit")
        ));
    }

    public Opt<String> validateEmail(String email) {
        return Opt.when(!EMAIL_VALIDATOR.isValid(email), "invalid email address");
    }

    public Opt<String> validateUsername(String username) {
        return Opt.when(!USERNAME_MATCHER.test(username), "invalid username format");
    }

    public Opt<String> validatePassword(String password, List<String> logins) {
        if (password.length() < MIN_PWD_LEN || password.length() > MAX_PWD_LEN)
            return Opt.of("password length must be in range [" + MIN_PWD_LEN + ", " + MAX_PWD_LEN + "]");
        for (Tuple2<Predicate<String>, String> passwordMatcher : PASSWORD_MATCHERS)
            if (!passwordMatcher.get1().test(password))
                return Opt.of("password must contain at least one " + passwordMatcher.get2());
        String lowerCasePassword = password.toLowerCase();
        for (String login : logins)
            if (lowerCasePassword.contains(login.toLowerCase()))
                return Opt.of("password must not contain login");
        return Opt.empty();
    }
}
