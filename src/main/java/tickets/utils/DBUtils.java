package tickets.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtils {
    private static Properties props = new Properties();

    static {
        try (InputStream input = DBUtils.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        String url = props.getProperty("jdbc.url");
        return DriverManager.getConnection(url);
    }
}
