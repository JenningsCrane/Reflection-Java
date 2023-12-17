package edu.school21.Annotations;

import com.zaxxer.hikari.HikariDataSource;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OrmManager {
    private final Connection connection;
    private String tableName;
    public OrmManager(HikariDataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
    }
    public void createTable() {
        try {
            Reflections reflections = new Reflections("edu.school21.Classes");
            Set<Class<?>> elements = reflections.getTypesAnnotatedWith(OrmEntity.class);

            for (Class<?> element : elements) {
                createTableForEntity(element);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void createTableForEntity(Class<?> entityClass) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            OrmEntity entity = entityClass.getAnnotation(OrmEntity.class);
            tableName = entity.table();

            String dropQuery = String.format("DROP TABLE IF EXISTS %s CASCADE;", tableName);
            statement.execute(dropQuery);
            System.out.println(dropQuery);

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(String.format("CREATE TABLE IF NOT EXISTS %s (", tableName));

            List<String> columnDefinitions = new ArrayList<>();

            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumnId.class)) {
                    columnDefinitions.add(String.format("%s SERIAL PRIMARY KEY", field.getName()));
                } else if (field.isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn column = field.getAnnotation(OrmColumn.class);
                    String columnDefinition = String.format("%s %s", column.name(), getColumnType(field));
                    columnDefinitions.add(columnDefinition);
                }
            }

            queryBuilder.append(String.join(", ", columnDefinitions));
            queryBuilder.append(");");
            statement.execute(queryBuilder.toString());
            System.out.println(queryBuilder);
        }
    }
    private String getColumnType(Field field) {
        OrmColumn column = field.getAnnotation(OrmColumn.class);
        if (column == null) {
            return "";
        }

        String fieldType = field.getType().getSimpleName();
        return switch (fieldType) {
            case "String" -> String.format("VARCHAR(%d)", column.length());
            case "Integer" -> "INTEGER";
            case "Long" -> "BIGINT";
            case "Boolean" -> "BOOLEAN";
            default -> "";
        };
    }
    public void save(Object entity) {
        Class<?> tmp = entity.getClass();
        Field[] fields = tmp.getDeclaredFields();
        if (!tmp.isAnnotationPresent(OrmEntity.class)) {
            System.err.println("Entity is not annotated!");
            return;
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(String.format("INSERT INTO %s (", this.tableName));

        List<String> columnNames = new ArrayList<>();
        List<String> columnValues = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(OrmColumn.class)) {
                OrmColumn column = field.getAnnotation(OrmColumn.class);
                columnNames.add(column.name());
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    columnValues.add(String.format("'%s'", value));
                } catch (IllegalAccessException e) {
                    System.err.println(e.getMessage());
                }
                field.setAccessible(false);
            }
        }

        queryBuilder.append(String.join(", ", columnNames));
        queryBuilder.append(") VALUES (");
        queryBuilder.append(String.join(", ", columnValues));
        queryBuilder.append(");");

        try {
            Statement statement = connection.createStatement();
            statement.execute(queryBuilder.toString());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        System.out.println(queryBuilder);
    }

    public void update(Object entity) {
        StringBuilder queryBuilder = new StringBuilder();
        List<String> updateClauses = new ArrayList<>();
        String idColumnName = null;
        Object idValue = null;

        Class<?> tmp = entity.getClass();

        Field[] fields = tmp.getDeclaredFields();

        queryBuilder.append(String.format("UPDATE %s SET ", this.tableName));

        for (Field field : fields) {
            if (field.isAnnotationPresent(OrmColumn.class)) {
                OrmColumn column = field.getAnnotation(OrmColumn.class);

                String columnName = column.name();

                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    updateClauses.add(String.format("%s = '%s'", columnName, value));
                } catch (IllegalAccessException e) {
                    field.setAccessible(false);
                    System.err.println(e.getMessage());
                }
                field.setAccessible(false);
            }
            else if (field.isAnnotationPresent(OrmColumnId.class)) {
                OrmColumnId columnId = field.getAnnotation(OrmColumnId.class);

                String columnName = columnId.name();

                field.setAccessible(true);
                try {
                    Object value = field.get(entity);

                    idColumnName = columnName;
                    idValue = value;
                } catch (IllegalAccessException e) {
                    field.setAccessible(false);
                    System.err.println(e.getMessage());
                }
                field.setAccessible(false);
            }
        }

        // Добавляем в запрос фрагмент с обновлением значений
        queryBuilder.append(String.join(", ", updateClauses));

        // Добавляем в запрос условие WHERE с идентификатором
        queryBuilder.append(String.format(" WHERE %s = '%s';", idColumnName, idValue));

        try {
            // Создаем объект Statement для выполнения SQL-запроса
            Statement statement = connection.createStatement();

            // Выполняем сформированный SQL-запрос
            statement.execute(queryBuilder.toString());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        // Выводим сформированный SQL-запрос в консоль
        System.out.println(queryBuilder);
    }


    public <T> T findById(Long id, Class<T> clazz) {
        if (!clazz.isAnnotationPresent(OrmEntity.class)) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(String.format("SELECT * FROM %s WHERE id = %d", this.tableName, id));
        T object = null;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
            System.out.println(queryBuilder);
            if (!resultSet.next()) {
                System.err.println("No such entity!");
                return null;
            }
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(OrmColumnId.class)) {
                    field.set(object, resultSet.getInt(1));
                } else if (field.isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn column = field.getAnnotation(OrmColumn.class);
                    field.set(object, resultSet.getObject(column.name()));
                }
            }
        } catch (SQLException | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
        return object;
    }
}
