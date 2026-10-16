module ni.edu.uam.papeleria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Paquete principal
    opens ni.edu.uam.papeleria to javafx.fxml;
    exports ni.edu.uam.papeleria;

    // Apertura del controlador para la inyección de componentes @FXML
    opens ni.edu.uam.papeleria.controller to javafx.fxml;
    exports ni.edu.uam.papeleria.controller;

    // Apertura del modelo para la lectura de propiedades desde el TableView
    opens ni.edu.uam.papeleria.model to javafx.base;
    exports ni.edu.uam.papeleria.model;

    // Exportación de las capas de acceso a datos y conexión
    exports ni.edu.uam.papeleria.dao;
    exports ni.edu.uam.papeleria.connection;
}