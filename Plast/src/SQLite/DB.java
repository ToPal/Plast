/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Vector;


/**
 *
 * @author nik
 */
public class DB {
    
    //Настройки соединения с БД SQLite.
    private static final String CLIENT_SQLITE_DB_FILE = "src/client/dbfile.db3";
    
    private static Database instance = DB.ClientDB_create_sqlite();
    

    //Далее алиасы для вызова функций instance'а. Сделаны для возможности статического к ним доступа
    public static Vector<String> get_column(String q) {
        return DB.instance.get_column(q);
    }

    public static String get_element(String q) {
        return instance.get_element(q);
    }

    public static Map<String, String> get_row(String q) {
        return instance.get_row(q);
    }

    public static Vector<Map<String, String>> get_table(String q) {
        return instance.get_table(q);
    }

    public static void query(String q) {
        instance.query(q);
    }
    
    /*
     * Инициализация Standalon'a и передача "родительскому" классу Database функции
     * получения соединения. БД - SQLite.
     */ 
    static Database ClientDB_create_sqlite() {
        if (instance == null) {
            Establish_connection_function ecf = new Establish_connection_function() {

                @Override
                public Connection establish_connection() {
                    try {
                        System.out.println("ClientDB_create_sqlite: try block started");
                        Class.forName("org.sqlite.JDBC");
                        String str_conn = "jdbc:sqlite:%s";
                        str_conn = String.format(str_conn, DB.CLIENT_SQLITE_DB_FILE);
                        System.out.println(str_conn);
                        Connection connection = DriverManager.getConnection(str_conn);
                        if (connection == null) {
                            System.out.println("ClientDB_create_sqlite: try block. Connection is NULL!");
                        }
                        return connection;
                    } catch (Exception e) {
                        String log = "Establishing connection with database error"; // Нужно добавлять дату и время в логи
                        System.out.println(log);
                        return null;
                    }
                }
            };
            return new Database(ecf);
        } else {
            System.out.println("instance not null");
            return instance;
        }
    }
    
}
