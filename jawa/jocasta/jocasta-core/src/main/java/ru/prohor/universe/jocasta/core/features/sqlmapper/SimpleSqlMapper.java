package ru.prohor.universe.jocasta.core.features.sqlmapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class for mapping entities to SQL queries.<br><br>
 * Example of an entity class:
 * <pre>
 * {@code
 * @Table(name = "users")
 * public class User implements Entity {
 *     @PrimaryKey
 *     @EntityField(name = "chat_id")
 *     private Long chatId;
 *     @EntityField(name = "chat_type")
 *     private String chatType;
 *     @EntityField(name = "chat_name")
 *     private String chatName;
 *     @EntityField(name = "user_link")
 *     private String userLink;
 * }
 * }
 * </pre>
 */
public class SimpleSqlMapper {
    public static <T extends Entity> boolean contains(Connection connection, Class<T> clazz, Object primaryKey)
            throws SQLException {
        return count(connection, clazz, primaryKey) > 0;
    }

    public static <T extends Entity> boolean containsByFields(Connection connection, T entity) throws SQLException {
        return countByFields(connection, entity) > 0;
    }

    public static <T extends Entity> int abstractCount(Connection connection, Class<T> clazz, String condition)
            throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sqlQuery = "SELECT COUNT(*) FROM " + getTableName(clazz) + condition;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            if (resultSet.next())
                return resultSet.getInt(1);
            return 0;
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }

    public static <T extends Entity> int count(Connection connection, Class<T> clazz)
            throws SQLException {
        return abstractCount(connection, clazz, ";");
    }

    public static <T extends Entity> int count(Connection connection, Class<T> clazz, Object primaryKey)
            throws SQLException {
        return abstractCount(connection, clazz, getPrimaryKeyCondition(clazz, primaryKey));
    }

    public static <T extends Entity> int countByFields(Connection connection, T entity) throws SQLException {
        return abstractCount(connection, entity.getClass(), getConditionByFields(entity));
    }

    public static <T extends Entity> void abstractDelete(Connection connection, Class<T> clazz, String condition)
            throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = "DELETE FROM " + getTableName(clazz) + condition;
            statement.execute(sql);
        }
    }

    public static <T extends Entity> void delete(Connection connection, Class<T> clazz, Object primaryKey)
            throws SQLException {
        abstractDelete(connection, clazz, getPrimaryKeyCondition(clazz, primaryKey));
    }

    public static <T extends Entity> void deleteByFields(Connection connection, T entity)
            throws SQLException {
        abstractDelete(connection, entity.getClass(), getConditionByFields(entity));
    }

    public static <T extends Entity> T get(Connection connection, Class<T> clazz, Object primaryKey)
            throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sqlQuery = "SELECT * FROM " + getTableName(clazz) + getPrimaryKeyCondition(clazz, primaryKey);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            if (resultSet.next())
                return createEntityFromResultSet(clazz, resultSet);
            throw new MappingException("entity with specified primary key does not exist");
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> T getOneByFields(Connection connection, T entity) throws SQLException {
        T t;

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sqlQuery = "SELECT * FROM " + getTableName(entity.getClass()) + getConditionByFields(entity);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            if (resultSet.next())
                t = (T) createEntityFromResultSet(entity.getClass(), resultSet);
            else
                throw new MappingException("there are no entities in database that have a field " +
                        "with the specified value");
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<T> getAllByFields(Connection connection, T entity) throws SQLException {
        List<T> list = new ArrayList<>();

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sqlQuery = "SELECT * FROM " + getTableName(entity.getClass()) + getConditionByFields(entity);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next())
                list.add((T) createEntityFromResultSet(entity.getClass(), resultSet));
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
        return list;
    }

    public static <T extends Entity> List<T> getAll(Connection connection, Class<T> clazz)
            throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            List<T> entities = new ArrayList<>();
            String sqlQuery = "SELECT * FROM " + getTableName(clazz) + ";";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                entities.add(createEntityFromResultSet(clazz, resultSet));
            }
            return entities;
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }

    public static <T extends Entity> void create(Connection connection, T entity) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            Map<String, String> map = new HashMap<>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                EntityField entityField = field.getAnnotation(EntityField.class);
                if (entityField == null)
                    continue;
                Object value;
                try {
                    value = field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new MappingException("error while reading value from entity", e);
                }
                if (value == null)
                    continue;
                map.put(entityField.name(), wrapObject(value));
            }
            if (map.isEmpty())
                throw new MappingException("entity " + entity + " has no writable fields");

            StringBuilder values = new StringBuilder();
            StringBuilder command = new StringBuilder();
            command.append("INSERT INTO ").append(getTableName(entity.getClass())).append(" (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                command.append(entry.getKey()).append(", ");
                values.append(entry.getValue()).append(", ");
            }
            values.setLength(values.length() - 2);
            command.setLength(command.length() - 2);
            command.append(") VALUES (").append(values).append(");");

            statement.execute(command.toString());
        }
    }

    public static <T extends Entity> void update(Connection connection, T entity) throws SQLException {
        Object primaryKey = getPrimaryKey(entity);
        if (!contains(connection, entity.getClass(), primaryKey)) {
            create(connection, entity);
            return;
        }

        try (Statement statement = connection.createStatement()) {
            Entity existing = get(connection, entity.getClass(), primaryKey);
            Map<String, String> map = new HashMap<>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                EntityField entityField = field.getAnnotation(EntityField.class);
                if (entityField == null)
                    continue;

                try {
                    Object entityFieldValue = field.get(entity);
                    Object existingFieldValue = field.get(existing);
                    if (existingFieldValue != null && !existingFieldValue.equals(entityFieldValue))
                        map.put(entityField.name(), wrapObject(entityFieldValue));
                } catch (IllegalAccessException e) {
                    throw new MappingException("error while extracting field for update", e);
                }
            }
            if (map.isEmpty())
                throw new MappingException("entities are identical");

            String sql = "UPDATE " +
                    getTableName(entity.getClass()) +
                    " SET " +
                    map.entrySet().stream()
                            .map(x -> x.getKey() + " = " + x.getValue())
                            .collect(Collectors.joining(", ")) +
                    " WHERE " +
                    getPrimaryKeyColumnName(entity.getClass()) +
                    " = " +
                    getPrimaryKey(entity) +
                    ";";
            statement.execute(sql);
        }
    }

    private static <T extends Entity> String getConditionByFields(T entity) {
        List<Object> values = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            Object value;
            field.setAccessible(true);
            EntityField entityField = field.getAnnotation(EntityField.class);
            if (entityField == null)
                continue;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null)
                continue;
            values.add(value);
            columns.add(entityField.name());
        }
        if (columns.isEmpty())
            throw new MappingException("entity hasn't not null fields");

        return " WHERE " + IntStream.range(0, columns.size())
                .mapToObj(i -> columns.get(i) + " = " + wrapObject(values.get(i)))
                .collect(Collectors.joining(" AND ")) + ";";
    }

    private static <T extends Entity> String getPrimaryKeyCondition(Class<T> clazz, Object primaryKey) {
        return " WHERE " + getPrimaryKeyColumnName(clazz) + " = " + wrapObject(primaryKey) + ";";
    }

    private static String getPrimaryKeyColumnName(Class<? extends Entity> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey == null)
                continue;
            EntityField entityField = field.getAnnotation(EntityField.class);
            if (entityField == null)
                throw new MappingException("primary key field must be annotated with " + EntityField.class);
            return Objects.requireNonNull(entityField.name(), "field " + field.getName() + " name is null in " + clazz);
        }
        throw new MappingException("class " + EntityField.class + " does not contain a primary key");
    }

    private static Object getPrimaryKey(Entity entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey == null)
                continue;
            EntityField entityField = field.getAnnotation(EntityField.class);
            if (entityField == null)
                throw new MappingException(
                        "primary key field must be annotated with " + EntityField.class + " in " + entity.getClass());
            try {
                return Objects.requireNonNull(field.get(entity), "primary key of " + entity + " is null");
            } catch (IllegalAccessException e) {
                throw new MappingException("error while extracting primary key", e);
            }
        }
        throw new MappingException("entity " + entity + " does not contain a primary key");
    }

    private static String getTableName(Class<? extends Entity> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null)
            throw new MappingException("class " + clazz + " is not a table");
        return Objects.requireNonNull(table.name(), "table name is null in " + clazz);
    }

    private static String wrapObject(Object object) {
        if (object instanceof String) {
            return "'" + object.toString().replaceAll("'", "''") + "'";
        } else if (object instanceof Number) {
            return object.toString();
        } else if (object instanceof LocalDate || object instanceof LocalTime) {
            return "'" + object + "'";
        } else if (object instanceof Boolean bool) {
            return bool ? "TRUE" : "FALSE";
        } else {
            throw new MappingException("unsupported type: " + object.getClass());
        }
    }

    private static <T extends Entity> T createEntityFromResultSet(Class<T> clazz, ResultSet resultSet)
            throws SQLException {
        T entity = instantiateEntity(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            EntityField entityField = field.getAnnotation(EntityField.class);
            if (entityField == null)
                continue;

            try {
                field.set(entity, resultSet.getObject(entityField.name(), field.getType()));
            } catch (IllegalAccessException e) {
                throw new MappingException("error while forming an entity", e);
            }
        }
        return entity;
    }

    private static <T extends Entity> T instantiateEntity(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new MappingException("class " + clazz + " does not contains no args constructor");
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new MappingException("error while invoke entity constructor", e);
        }
    }
}
