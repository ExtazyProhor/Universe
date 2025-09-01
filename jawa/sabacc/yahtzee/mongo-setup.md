# MongoDB setup

### User and Database

```js
use ${database}

db.createUser({
    user: "${user}",
    pwd: "${password}",
    roles: [
        {
            role: "readWrite",
            db: "${database}"
        }
    ]
})

db.getUsers()

exit
```

### Collections and Indexes

```js
db.createCollection("users")
db.createCollection("images")
db.createCollection("irl_games")
db.createCollection("irl_rooms")

db.users.createIndex(
    {
        username: "text",
        display_name: "text"
    },
    {
        default_language: "english",
        weights: { username: 3, display_name: 1 }
    }
)

db.users.getIndexes()
```
