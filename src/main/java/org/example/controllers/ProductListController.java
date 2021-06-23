package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.SneakyThrows;
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

import static org.example.entities.Warehouse.cTypes.*;


public class ProductListController {
    int counter=0;

    @FXML
    private Button update;
    @FXML
    private Button delete;
    @FXML
    private Button create;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab products_tab;
    @FXML
    private Tab stats;

    @FXML
    private Label statusLabel;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn nameCo;
    @FXML
    private TableColumn priceFrom;
    @FXML
    private TableColumn priceTo;
    @FXML
    private TableColumn amountFrom;
    @FXML
    private TableColumn amountTo;
    @FXML
    private TextField name;
    @FXML
    void deleteProduct(ActionEvent event) throws Exception {
       Product product = productTable.getSelectionModel().getSelectedItem();

        if(product != null) {
            Message msg = new Message(DELETE_PRODUCT.ordinal(), 1, product.getId_group()+"");
            Packet packet = new Packet((byte) 1, Generator.packetId, msg);
            Generator.packetId = Generator.packetId.plus(UnsignedLong.valueOf(1));
            StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
            Thread t1 = new Thread(client1);
            t1.start();
            //t1.join();
            packet.encodePackage();
            Packet receivedPacket = client1.sendMessage(packet);
            int command = receivedPacket.getBMsq().getCType();
            Warehouse.cTypes[] val = Warehouse.cTypes.values();
            Warehouse.cTypes command_type = val[command];
            if (command_type == DELETE_PRODUCT) {
                String message = receivedPacket.getBMsq().getMessage();
                JSONObject information = new JSONObject(message);
                try {
                    System.out.println(information.getString("message"));
                    //statusLabel.setText(information.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                statusLabel.setText("Can't delete product.");
            }
            resetTable();
        }else{
            statusLabel.setText("Choose product to delete!");
        }
    }
    @FXML
    void updateProduct(ActionEvent event) throws Exception {
        statusLabel.setText("");

        Product product = productTable.getSelectionModel().getSelectedItem();

        if(product != null) {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/org/example/ui/UpdateProductView.fxml").toURI().toURL();
            loader.setLocation(url);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update product");

            UpdateProductController controller = loader.getController();
            controller.initData(product);

            stage.setOnHiding(new EventHandler<WindowEvent>() {
                @SneakyThrows
                public void handle(WindowEvent we) {
                    resetTable();
                }
            });

            stage.show();
        }else{
            statusLabel.setText("Choose product to update!");
        }
    }
    @FXML
    void find1(ActionEvent event) throws Exception {
        Message msg = new Message(GET_PRODUCT.ordinal(), 1, name.getText());
        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
        Generator.packetId = Generator.packetId.plus(UnsignedLong.valueOf(1));
        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
        Thread t1 = new Thread(client1);
        t1.start();
        //t1.join();
        packet.encodePackage();
        Packet receivedPacket = client1.sendMessage(packet);
        int command = receivedPacket.getBMsq().getCType();
        Warehouse.cTypes[] val = Warehouse.cTypes.values();
        Warehouse.cTypes command_type = val[command];

        if (command_type == GET_PRODUCT) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);

            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");

                List<Product> products = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    products.add(new Product(array.getJSONObject(i)));
                    System.out.println(products.get(i));
                }

                productTable.getItems().clear();
                productTable.getItems().addAll(products);

            } catch (JSONException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to get products!");
            }
        } else {
            statusLabel.setText("Failed to get products!");
        }
    }
    @FXML
    void initialize() throws Exception {
      //  nameCo.setCellValueFactory(new PropertyValueFactory<>("name"));
      //  priceFrom.setCellValueFactory(new PropertyValueFactory<>("price_from"));
       // priceTo.setCellValueFactory(new PropertyValueFactory<>("price_To"));
       // amountFrom.setCellValueFactory(new PropertyValueFactory<>("amount_from"));
        //amountTo.setCellValueFactory(new PropertyValueFactory<>("amount_to"));
        resetTable();
    }
    @FXML
    void createProduct(ActionEvent event) throws Exception {
        // statusLabel.setText("");

        URL url = new File("src/main/java/org/example/ui/NewProductView.fxml").toURI().toURL();
        Parent root = null;
        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Function is not available.");
        }
        Stage stage = new Stage();
        stage.setTitle("New Product");
        stage.setScene(new Scene(root));

        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @SneakyThrows
            public void handle(WindowEvent we) {
                resetTable();
            }
        });

        stage.show();
    }
    private void resetTable() throws Exception {
        Message msg = new Message(GET_LIST_PRODUCTS.ordinal(), 1, "");
        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
        Generator.packetId = Generator.packetId.plus(UnsignedLong.valueOf(1));
        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
        //Packet receivedPacket = GlobalContext.clientTCP.sendPacket(packet.toPacket());
        Thread t1 = new Thread(client1);
        t1.start();
        //t1.join();
        packet.encodePackage();
        Packet receivedPacket = client1.sendMessage(packet);
        int command = receivedPacket.getBMsq().getCType();
        Warehouse.cTypes[] val = Warehouse.cTypes.values();
        Warehouse.cTypes command_type = val[command];

        if (command_type == GET_LIST_PRODUCTS) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);

            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");

                List<Product> products = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    products.add(new Product(array.getJSONObject(i)));
                    System.out.println(products.get(i));
                }

                productTable.getItems().clear();
                productTable.getItems().addAll(products);

            } catch (JSONException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to get products!");
            }
        } else {
            statusLabel.setText("Failed to get products!");
        }
    }
    @FXML
    private void groupsChange() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = (Stage) update.getScene().getWindow();
        URL url = new File("src/main/java/org/example/ui/groupView.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
    //statisticsChanged
    @FXML
    private void statisticsChanged() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = (Stage) create.getScene().getWindow();
        URL url = new File("src/main/java/org/example/ui/stats.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
}
