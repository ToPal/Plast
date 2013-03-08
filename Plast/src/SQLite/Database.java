package SQLite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/*
 * @author nik
 */
public class Database {
    
    //Объект соединения
    private Connection connection = null;
    
    Establish_connection_function establish_connection_function;
    
    public Database(Establish_connection_function f) {
        establish_connection_function = f;
    }

    
    //Следующая функция устанавливает соединение с БД
    protected boolean establish_connection() {
        if (connection != null) {
            System.out.println("Database. Establish_connection: connection NOT null");
            return true;
        }
        System.out.println("Database. Establish_connection: connection IS null");
        connection = establish_connection_function.establish_connection();
        return false;
    }
    
    /*
     * Функция выполняет SQL-запрос и возвращает результат в стандартном формате
     * драйвера mysql
     */
    private ResultSet process_query(String q) {
        
        Statement st = null;
        ResultSet rs = null;
        try {
            if ( connection == null ) {
                System.out.println("Database. Process_query. Connection is null");
                establish_connection();
                //process_query("SET NAMES utf8");
            }
            st = connection.createStatement();
            rs = st.executeQuery(q);
            
            //st.close(); //!!!!!!!!!!!!!!!!!!!!!!!!!
            //connection.close(); //!!!!!!!!!!!!!!!!!
        } catch(Exception e) {
        } finally {
            return rs;
            
            
        }
    }
    
    
    //Следующая функция закрывает соединение с БД
    public void destroy() {
        try {
            connection.close();
        } finally {
            return;
        }
    }
    
    
    /*
     * Выполнить SQL-запрос, без ответа
     */
    
    public void query(String q) {
        process_query(q);
    }
    
    /*
     * Выполнить SQL-запрос и вернуть единственный элемент
     */
    public String get_element(String q) {
        String res = "";
        ResultSet rs = process_query(q);
        try {
            if (rs.next()) {
                res = rs.getString(1);
            }
        } finally {
            return res;
        }
    }
    
    
    /*
     * Выполнить SQL-запрос и вернуть строку
     */
    public Map<String, String> get_row(String q) {
        Map<String, String> res = new HashMap<>();
        ResultSet rs = process_query(q);
        try {
            rs.next();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                res.put(rs.getMetaData().getColumnLabel(i), rs.getString(i));
            }
        } finally {
            return res;
        }
    }
    
    /*
     * Выполнить SQL-запрос и вернуть вектор со значениями
     */
    public Vector<Map<String, String>> get_table(String q) {
        Vector<Map<String, String>> res = new Vector<>();
        ResultSet rs = process_query(q);
        try {
            Map<String, String> row;
            while(rs.next()) {
                row = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.put(rs.getMetaData().getColumnLabel(i), rs.getString(i));
                }
                res.add(row);
            }
        } finally {
            return res;
        }
    }
    
    /*
     * Выполнить SQL-запрос и вернуть вектор со значениями столбца
     */
    public Vector<String> get_column(String q) {
        Vector<String> res = new Vector<>();
        ResultSet rs = process_query(q);
        try {
            while(rs.next()) {
                System.out.println(rs.getString(q));
                res.add(rs.getString(q));
            }
        } finally {
            return res;
        }
    }
}
