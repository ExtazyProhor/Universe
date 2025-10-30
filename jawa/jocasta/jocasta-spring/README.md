# Jocasta Spring 

Библиотека для работы со spring-ом

## Features

### Jocasta Holocron

![](assets/holocron.webp)

Этот модуль предназначен для подстановки секретов
в значения свойств из файлов `application.properties`.

#### Подключение

##### Импорт конфигурации

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        HolocronConfiguration.class
})
public class MyConfiguration {}
```

##### Указание местоположения файла с секретами

```properties
# для local
universe.holocron.file-path=${user.home}/my/holocron.json
```

```properties
# для stable
universe.holocron.file-path=/etc/universe/holocron.json
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
