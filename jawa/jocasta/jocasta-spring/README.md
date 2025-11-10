# Jocasta Spring 

Библиотека для работы со spring-ом

## Features

### Jocasta Holocron

![](assets/holocron.webp)

Этот модуль предназначен для подстановки секретов
в значения свойств из файлов `application.properties`.

#### Подключение (импорт конфигурации)

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        HolocronConfiguration.class
})
public class MyConfiguration {}
```

#### Использование

Необходимо определить значение секрета в файле с
секретами (`holocron.json`):

```json
{
  "json.key": "SecretValue123"
}
```

Теперь в файлах `application.properties` можно
указывать свойства вида:

```properties
property.key=holocron:{json.key}
```

И использовать их при создании бинов:

```java

@Bean
public MyBean myBean(
        @Value("${property.key}") String secret
) {
    return new MyBean(secret);
}
```
