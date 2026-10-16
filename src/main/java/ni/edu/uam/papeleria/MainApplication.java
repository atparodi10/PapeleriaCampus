package ni.edu.uam.papeleria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("/ni/edu/uam/papeleria/producto-view.fxml")
        );

        Rectangle2D limitesPantalla = Screen.getPrimary().getVisualBounds();
        double ancho = limitesPantalla.getWidth() * 0.9;
        double alto = limitesPantalla.getHeight() * 0.9;

        Scene scene = new Scene(fxmlLoader.load(), ancho, alto);
        stage.setTitle("Catálogo de Productos — Papelería Campus");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}