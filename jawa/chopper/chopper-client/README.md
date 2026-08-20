# Chopper client

Клиент для взаимодействия с chopper API

![](assets/chopper.webp)

### Подключение

`pom.xml`:

```xml

<dependencies>
    ...
    <dependency>
        <groupId>ru.prohor.universe</groupId>
        <artifactId>chopper-client</artifactId>
    </dependency>
</dependencies>
```

`application.properties`:

```properties
# для local
spring.config.import=classpath:chopper-client-local.properties
```

```properties
# для stable
spring.config.import=classpath:chopper-client-stable.properties
```

Импорт конфигурации:

```kotlin
@Configuration
@Import(
    ChopperClientConfig::class
)
class MyConfiguration
```

### Использование

Получить бин `ChopperClient` и использовать его методы:

```kotlin
class SomeClass(
    private val chopperClient: ChopperClient
) {
    // просто текст
    fun text() {
        val result = chopperClient.sendMessage(
            text = "Notification Text",
            chatId = 123456789
        )
        if (!result) {
            throw RuntimeException()
        }
    }

    // текст с markdown разметкой
    fun markdown() {
        val result = chopperClient.sendMessage(
            text = "*Notification* `Text`",
            chatId = 123456789,
            markdown = true
        )
        if (!result) {
            throw RuntimeException()
        }
    }

    // файл
    fun file(longText: String) {
        val result = chopperClient.sendFile(
            content = longText,
            chatId = 123456789,
            fileName = "readme.txt",
            caption = "подпись"
        )
        if (!result) {
            throw RuntimeException()
        }
    }
}
```
