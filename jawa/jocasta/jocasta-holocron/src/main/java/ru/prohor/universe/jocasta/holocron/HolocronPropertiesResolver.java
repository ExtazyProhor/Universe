package ru.prohor.universe.jocasta.holocron;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HolocronPropertiesResolver implements BeanFactoryPostProcessor {
    // TODO уметь загружать только один сервис и одно его окружение + default / common / share <-
    // TODO искать сначала в текущем окружении, затем в share, иначе null
    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<>() {};
    private static final Pattern HOLOCRON_PATTERN = Pattern.compile(
            "holocron:\\{([a-zA-Z\\-_.0-9<>,;:|*&^%$#@!]+)}"
    );

    private final Map<String, String> secrets;

    public HolocronPropertiesResolver(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> map = objectMapper.readValue(
                    Files.readString(Path.of(filePath)),
                    MAP_TYPE_REFERENCE
            );
            this.secrets = new HashMap<>(map);
        } catch (IOException e) {
            throw new RuntimeException("Error when reading holocron file {" + filePath + "}", e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.addEmbeddedValueResolver(this::processSecrets);
    }

    private String processSecrets(String value) {
        if (value == null)
            return null;

        Matcher matcher = HOLOCRON_PATTERN.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String secretKey = matcher.group(1);
            matcher.appendReplacement(
                    sb,
                    Opt.ofNullable(lookupSecret(secretKey)).map(Matcher::quoteReplacement).orElse("")
            );
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String lookupSecret(String secretKey) {
        return secrets.get(secretKey);
    }
}
