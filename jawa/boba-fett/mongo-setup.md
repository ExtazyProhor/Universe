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
db.createCollection("custom_holidays")

db.users.createIndex(
    {
        "chat_id": 1
    }
)

db.users.createIndex(
    {
        "chat_id": 1,
        "holidays_subscription_options.daily_distribution_time.hour": 1,
        "holidays_subscription_options.daily_distribution_time.minute": 1
    }
)

db.users.getIndexes()

db.custom_holidays.createIndex(
    {
        "chat_id": 1
    }
)

db.custom_holidays.getIndexes()
```
