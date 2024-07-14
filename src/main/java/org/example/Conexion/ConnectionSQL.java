package org.example.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSQL {
    private static ConnectionSQL instance;
    private static Connection connection;

    private ConnectionSQL(){

        String dbHost = "LAP_DEV";
        String dbPort = "1433";
        String dbName ="Biblioteca" ;
        String dbUser = "libro";
        String dbPassword = "123456" ;

        try {
            // carga el drive de SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String DBURI = "jdbc:sqlserver://" + dbHost + ":" + dbPort + ";databaseName=" + dbName + ";encrypt=false;trustServerCertificate=true";
            // establece la conexión
            connection = DriverManager.getConnection(DBURI, dbUser, dbPassword);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized ConnectionSQL getInstance() {
        if(instance == null){
            instance = new ConnectionSQL();
        }
        return instance;
    }

    public static Connection getConnection (){
        return connection;
    }
    public void closeConnection(){
        if(connection != null){
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
