# Fondor Kenobi

## Авторизация и API

Используется Scarif JWT: refresh cookie через `POST /api/auth/refresh`, access token только в памяти,
заголовок `Authorization: Bearer ...`. При 401 токен обновляется и запрос повторяется.
Для входа страница предлагает открыть Scarif.
Пользователь определяется исключительно из `AuthorizedUser.objectId`.

## Проверки

```sh
mvn -pl kenobi/kenobi-app,fondor/fondor-kenobi -am test
deno test fondor/fondor-kenobi/tests
deno run --allow-read --allow-write --allow-net --allow-run fondor/fondor-kenobi/tests/browser-check.js
```

## SPA-маршруты

- `/` - главная.
- `/training` - загрузка и прохождение подхода; прямой вход и обновление страницы запрашивают активный набор с backend.
- `/result` - сохранение и результат текущего подхода. Без завершённого подхода в памяти страницы перенаправляет на `/`.
