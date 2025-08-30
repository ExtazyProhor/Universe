# MongoDB setup

### User and Database

```
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

```
db.createCollection("users")

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
