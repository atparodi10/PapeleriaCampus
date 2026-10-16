package ni.edu.uam.papeleria.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    // Parámetros de conexión hacia la base de datos local
    private static final String URL = "jdbc:postgresql://localhost:5432/papeleria_campus";
    private static final String USER = "postgres";
    private static final String PASS = "Josetorres2005!";

    /**
     * Obtiene una nueva conexión activa a PostgreSQL.
     * Propaga SQLException para que el controlador informe al usuario ante fallos de conexión.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}