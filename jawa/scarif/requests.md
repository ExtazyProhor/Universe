# Использование JWT access-token-а в запросах

Сервисы в `Universe` используют единый сервер аутентификации
`scarif`. Для других сервисов он предоставляет
[jwt access-token](../hyperspace/scarif-jwt), в котором
есть вся необходимая информация о запросе.

### Подключение в `html`

```html

<script src="https://scarif.tima-prohorov.ru/files/js/requests.js"></script>
```

### Использование

Файл `requests.js` экспортирует 2 функции: `universeGet` и
`universePost`. Каждый запрос к сервису необходимо делать
через них. Пример их использования:

```javascript
universeGet('<serivce>', 'api/path')
    .then(r => r.json())
    .then(console.log)
    .catch(console.error);

universePost('<service>', 'api/path', { data: 123 })
    .then(r => r.json())
    .then(console.log)
    .catch(console.error);
```

Необходимо передать имя сервиса, путь запроса и, если запрос
делается методом `POST`, тело.

### Что делают функции

Функции автоматически обрабатывают истечение срока
access-token-а и ходят за новым с помощью refresh-token-а.
Если и его срок истек - возвращается ответ с кодом 401.
Это означает, что необходимо войти в аккаунт заново.
