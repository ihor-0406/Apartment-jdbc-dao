package apartments;

import apartments.shared.Id;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AbstractApartamentDAO <T> {
    private final Connection conn;
    private final String table;

    public AbstractApartamentDAO(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    public void createTable(Class<T> cls){
        Field[] fields = cls.getDeclaredFields();
        Field id = getPrimaryKeyField(null, fields);

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ")
                .append(table)
                .append("(");

        sql.append(id.getName())
                .append(" ")
                .append(" INT AUTO_INCREMENT PRIMARY KEY,");
        for(Field f : fields){
            if (f != id){
                f.setAccessible(true);
                sql.append(f.getName()).append(" ");

                if(f.getType() == int.class){
                    sql.append(" INT,");
                } else if (f.getType() == double.class) {
                    sql.append(" DOUBLE,");
                } else if (f.getType() == String.class) {
                    sql.append(" VARCHAR(100),");
                }else throw new RuntimeException("Wrong type");
            }
        }

        sql.deleteCharAt(sql.length()-1);
        sql.append(")");

        try{
            try (Statement st = conn.createStatement()){
                st.execute("DROP TABLE IF EXISTS " + table);
                st.execute(sql.toString());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void add( T t){
        try{
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKeyField(t, fields);

            StringBuilder  names = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for(Field f : fields){
                if (f != id){
                    f.setAccessible(true);
                    names.append(f.getName()).append(',');
                    values.append('"').append(f.get(t)).append("\",");
                }
            }
            names.deleteCharAt(names.length()-1);
            values.deleteCharAt(values.length()-1);

            String sql = "INSERT INTO " + table + "(" + names.toString() +
                    ") VALUES(" + values.toString() + ")";
            try(Statement st = conn.createStatement()){
                st.execute(sql);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public void update(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKeyField(t, fields);

            StringBuilder sb = new StringBuilder();

            for (Field f : fields) {
                if (f != id) {
                    f.setAccessible(true);

                    sb.append(f.getName())
                            .append(" = ")
                            .append('"')
                            .append(f.get(t))
                            .append('"')
                            .append(',');
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            // update t set name = "aaaa", age = "22" where id = 5
            String sql = "UPDATE " + table + " SET " + sb.toString() + " WHERE " +
                    id.getName() + " = \"" + id.get(t) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void delete(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKeyField(t, fields);

            // delete from t where id = x
            String sql = "DELETE FROM " + table + " WHERE " + id.getName() +
                    " = \"" + id.get(t) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<T> getAll(Class<T> cls) {
        List<T> res = new ArrayList<>();

        try {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + table)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T t = cls.getDeclaredConstructor().newInstance(); //!!!

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);
                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);

                            field.set(t, rs.getObject(columnName));
                        }

                        res.add(t);
                    }
                }
            }

            return res;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public List<T> getByPriceRange(Class<T> cls, long min, long max){
        String sql = "SELECT * FROM " + table + " WHERE price BETWEEN " + min + " AND " + max;
        return executeQuery(cls, sql);
    }
    public List<T> getByMinArea(Class<T> cls, long minArea){
        String sql = "SELECT * FROM " + table + " WHERE area > " + minArea;
        return executeQuery(cls, sql);
    }
    public  List<T> getByRooms (Class<T> cls, int rooms){
        String sql = "SELECT * FROM " + table + " WHERE rooms = " + rooms;
        return executeQuery(cls, sql);
    }
    private List<T> executeQuery(Class<T> cls, String sql){
        List<T> res = new ArrayList<>();

        try(Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){

            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()){

                T t = cls.getDeclaredConstructor().newInstance();

                for (int i = 1; i <= md.getColumnCount(); i++){
                    String columnName = md.getColumnName(i);

                    Field field = cls.getDeclaredField(columnName);
                    field.setAccessible(true);

                    Object value = rs.getObject(columnName);

                    if(field.getType() == int.class && value instanceof  Number){
                        field.set(t,((Number) value).intValue());
                    }else {
                        field.set(t, value);
                    }
                }
                res.add(t);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return res;
    }

    private Field getPrimaryKeyField(T t, Field[] fields){
        Field result = null;

        for(Field f : fields){
            if(f.isAnnotationPresent(Id.class)){
                result = f;
                result.setAccessible(true);
                break;
            }
        }
        if(result == null)
            throw new RuntimeException(" No Id field found");

            return result;

    }
}
