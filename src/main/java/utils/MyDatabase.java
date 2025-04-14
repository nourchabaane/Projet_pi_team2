package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    private final String url = "jdbc:mysql://localhost:3306/3A61";
    private final String user = "root";
    private final String password = "";
    private Connection cnx;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            cnx = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            cnx = null;
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion rétablie");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la rétablissement de la connexion: " + e.getMessage());
            cnx = null;
        }
        return cnx;
    }
}