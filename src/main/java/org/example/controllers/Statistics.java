package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Statistics {
    int counter=0;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab products_tab;
    @FXML
    private Tab stats;

    @FXML
    private void productsChange()
    {

    }
    //statisticsChanged
    @FXML
    private void groupChanged() throws IOException {
        if(counter!=0) {
            FXMLLoader loader = new FXMLLoader();
            Stage stage = (Stage) tabPane.getScene().getWindow();
            URL url = new File("src/main/java/org/example/ui/groupView.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        }
        counter++;
    }
    @FXML
    void initialize() throws Exception {
//        idCol.setCellValueFactory(new PropertyValueFactory<>("id_group"));
//        desc.setCellValueFactory(new PropertyValueFactory<>("description"));
//        nameCo.setCellValueFactory(new PropertyValueFactory<>("name"));
//        resetTable();
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(stats);
    }
}
