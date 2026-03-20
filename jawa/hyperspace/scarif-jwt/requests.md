# Использование JWT access-token-а в запросах

Сервисы в `Universe` используют единый сервер аутентификации
`scarif`. Для других сервисов он предоставляет
`jwt access-token`, в котором
есть вся необходимая информация о запросе.

### Подключение в `html`

```html

<script src="/requests.js"></script>
```

### Использование

Файл `requests.js` экспортирует функцию `universeFetch`.
Каждый запрос к сервису необходимо делать
через эту функцию. Пример использования:

```javascript
const resp = await universeFetch('api', 'users/me');
if (resp.ok) {
    const data = await resp.json();
}

await universeFetch('api', 'posts', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        title: 'Hello'
    }),
});
```

При загрузке страницы надо вызвать функцию `initAuth`:

```javascript
(async () => {
    const ok = await initAuth();
    if (!ok) {
        // кнопка войти
    }
})();
```

### Что делает функция

Функция автоматически обрабатывают истечение срока
access-token-а и ходят за новым с помощью refresh-token-а.
Если и его срок истек - возвращается ответ с кодом 401.
Это означает, что необходимо войти в аккаунт заново.
