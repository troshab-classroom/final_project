package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.TCP.StoreClientTCP;
import org.example.entities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.example.entities.Warehouse.cTypes.GET_LIST_GROUPS;
import static org.example.entities.Warehouse.cTypes.GET_PRODUCTS_STATISTICS;

public class Statistics {
    Group group;
    @FXML
    private TableView<ProductStatistics> productsTable;
    @FXML
    private Label totalCostLabel;
    @FXML
    private ChoiceBox<Group> groupChoice;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab products_tab;
    @FXML
    private Tab stats;
    @FXML
    private TableColumn<ProductStatistics, String> idCol;

    @FXML
    private TableColumn<ProductStatistics, String> nameCol;

    @FXML
    private TableColumn<ProductStatistics, String> priceCol;

    @FXML
    private TableColumn<ProductStatistics, String> amountCol;

    @FXML
    private TableColumn<ProductStatistics, String> descrCol;

    @FXML
    private TableColumn<ProductStatistics, String> manufacturerCol;
    @FXML
    private TableColumn<ProductStatistics, String> totalCostCol;
    @FXML
    private Label statusLabel;

    @FXML
    void showStatistics(ActionEvent event) throws Exception {
        try{
            groupChoice.getValue().getId_group();
        }catch(Exception e)
        {
            statusLabel.setText("Choose group");
            return;
        }
        resetTable(groupChoice.getValue().getId_group());
    }

    private void resetTable(Integer group_id) throws Exception {
        Double totalGroupCost = 0.0;

        Message msg = new Message(GET_PRODUCTS_STATISTICS.ordinal(), 1, group_id.toString());

        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
        Generator.packetId =Generator.packetId.plus(UnsignedLong.valueOf(1));
        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
        Thread t1 = new Thread(client1);
        t1.start();
        t1.join();
        packet.encodePackage();
        Packet receivedPacket = client1.sendMessage(packet);
        int command = receivedPacket.getBMsq().getCType();
        Warehouse.cTypes[] val = Warehouse.cTypes.values();
        Warehouse.cTypes command_type = val[command];

        if (command_type == GET_PRODUCTS_STATISTICS) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);
            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");

                List<ProductStatistics> products = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    ProductStatistics productStatistics = new ProductStatistics(array.getJSONObject(i));
                    totalGroupCost += productStatistics.getTotal_cost();
                    products.add(productStatistics);
                }
                productsTable.getItems().clear();
                productsTable.getItems().addAll(products);
                totalCostLabel.setText("Total cost of products in group: " + totalGroupCost.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                statusLabel.setText(information.getString("message"));
            }

        } else {
            statusLabel.setText("Failed to calculate statistics!");
        }
    }
    @FXML
    private void productsChange() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = (Stage) tabPane.getScene().getWindow();
        URL url = new File("src/main/java/org/example/ui/ProductListView.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
    @FXML
    private void groupChanged() throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader();
            Stage stage = (Stage) tabPane.getScene().getWindow();
            URL url = new File("src/main/java/org/example/ui/groupView.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            stage.setScene(scene);
    }catch(Exception e)
    {
    }
    }
    @FXML
    void initialize() throws Exception {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descrCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        manufacturerCol.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("total_cost"));
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(stats);
        Message msg = new Message(GET_LIST_GROUPS.ordinal(), 1, "");
        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
        Generator.packetId = Generator.packetId.plus(UnsignedLong.valueOf(1));
        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
        Thread t1 = new Thread(client1);
        t1.start();
        packet.encodePackage();
        Packet receivedPacket = client1.sendMessage(packet);

        int command = receivedPacket.getBMsq().getCType();
        Warehouse.cTypes[] val = Warehouse.cTypes.values();
        Warehouse.cTypes command_type = val[command];

        if (command_type == GET_LIST_GROUPS) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);
            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");
                ObservableList<Group> groups = FXCollections.observableArrayList();
                for (int i = 0; i < array.length(); i++) {
                    groups.add(new Group(array.getJSONObject(i)));
                }
                groupChoice.setItems(groups);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void signOut() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        URL url = new File("src/main/java/org/example/ui/loginView.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
}
