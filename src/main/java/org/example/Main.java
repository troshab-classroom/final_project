package org.example;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        try {
            URL url = new File("src/main/java/org/example/ui/test.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Storehouse");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        launch(args);
    }

}