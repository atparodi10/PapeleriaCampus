package ni.edu.uam.papeleria.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DataBaseConnection {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/papeleria_campus";
    private static final String PROPERTIES_FILE = "db.properties";

    public static Connection getConnection() throws SQLException {
        Properties propiedades = cargarPropiedades();

        String url = propiedades.getProperty("db.url", DEFAULT_URL).trim();
        String user = propiedades.getProperty("db.user", "postgres").trim();
        String password = propiedades.getProperty("db.password", "");

        return DriverManager.getConnection(url, user, password);
    }


    private static Properties cargarPropiedades() throws SQLException {
        Properties propiedades = new Properties();
        Path rutaArchivo = Paths.get(PROPERTIES_FILE);

        if (Files.exists(rutaArchivo)) {
            try (InputStream input = new FileInputStream(rutaArchivo.toFile())) {
                propiedades.load(input);
                return propiedades;
            } catch (IOException e) {
                throw new SQLException("Error al leer el archivo local db.properties.", "08001", e);
            }
        }

        try (InputStream recursoClasspath = DataBaseConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (recursoClasspath != null) {
                propiedades.load(recursoClasspath);
                return propiedades;
            }
        } catch (IOException e) {
            throw new SQLException("Error al cargar db.properties desde el classpath.", "08001", e);
        }

        throw new SQLException(
                "No se encontró el archivo 'db.properties' en la raíz del proyecto. " +
                        "Cree una copia basada en 'db.properties.example' con sus credenciales locales.",
                "08001"
        );
    }
}