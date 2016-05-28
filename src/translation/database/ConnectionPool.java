package translation.database;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private BasicDataSource dataSource;

    public static ConnectionPool instance;

    private ConnectionPool(){

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUsername("ADMIN-PC\\SQLEXPRESS");
        dataSource.setPassword("123456");
        dataSource.setUrl("jdbc:sqlserver://localhost:1433;databaseName=MT;integratedSecurity=true;");

        dataSource.setInitialSize(2);
        dataSource.setMaxIdle(10);
        dataSource.setMaxTotal(15);
    }


    public static synchronized ConnectionPool getInstance(){
        if (instance == null) return new ConnectionPool();
        return instance;
    }


    public synchronized Connection getConnection() throws PersistException {
        try{
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new PersistException(e);
        }
    }
}
