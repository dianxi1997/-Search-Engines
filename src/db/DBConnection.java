package db;


import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接层MYSQL
 * @author Administrator
 *
 */
public class DBConnection {    
    
    /**
     * 连接数据库
     * @return
     */
    public static Connection getDBConnection()
    {
        // 1. 注册驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 获取数据库的连接
        try {
            Connection conn  = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/movie?useUnicode=true&characterEncoding=utf-8", "rwq414", "rwq216127");
            return conn;
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return null;
    }
    
}
