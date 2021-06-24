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
import org.example.ProductCriteria;
import org.example.TCP.StoreClientTCP;
import org.example.entities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.example.entities.Warehouse.cTypes.*;


public class ProductListController {
    int counter=0;
    @FXML
    private TextField name;
    @FXML
    private TextField priceFrom;
    @FXML
    private TextField priceTo;
    @FXML
    private TextField amountFrom;
    @FXML
    private TextField amountTo;
    @FXML
    private TextField manufacturer;
    @FXML
    private TextField descr;
    @FXML
    private TextField groupName;
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
    private TableColumn<Object, Object> idCol;
    @FXML
    private TableColumn<Object, Object> nameCol;
    @FXML
    private TableColumn<Object, Object> priceCol;
    @FXML
    private TableColumn<Object, Object> amountCol;
    @FXML
    private TableColumn<Object, Object> manufCol;
    @FXML
    private TableColumn<Object, Object> descrCol;
    @FXML
    private TableColumn<Object, Object> groupCol;

    @FXML
    void addAmount(ActionEvent event) throws MalformedURLException {
        Product product = productTable.getSelectionModel().getSelectedItem();

        if (product != null) {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/org/example/ui/AddAmountView.fxml").toURI().toURL();
            loader.setLocation(url);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add amount");

            AddAmountController controller = loader.getController();
            controller.initData(product);

            stage.setOnHiding(new EventHandler<WindowEvent>() {
                @SneakyThrows
                public void handle(WindowEvent we) {
                    resetTable();
                }
            });

            stage.show();
        } else {
            statusLabel.setText("Choose product first!");
        }
    }

    @FXML
    void deductAmount(ActionEvent event) throws MalformedURLException {
        Product product = productTable.getSelectionModel().getSelectedItem();

        if (product != null) {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/org/example/ui/loginView.fxml").toURI().toURL();
            loader.setLocation(url);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Deduct amount");

            //DeductAmountController controller = loader.getController();
            //controller.initData(product);

            stage.setOnHiding(new EventHandler<WindowEvent>() {
                @SneakyThrows
                public void handle(WindowEvent we) {
                    resetTable();
                }
            });

            stage.show();
        }else{
            statusLabel.setText("Choose product first.");
        }
    }
    @FXML
    void filterProducts(ActionEvent event) throws Exception {
        statusLabel.setText("");

        List<Integer> listId = new ArrayList<Integer>();
        ProductCriteria fl = new ProductCriteria();
        if (!name.getText().isEmpty()) {
           fl.setTitle(name.getText());
        }

        if (!priceFrom.getText().isEmpty()) {
            try {
                double price = Double.parseDouble(priceFrom.getText());
                if (price >= 0) {
                    fl.setPriceFrom(price);
                } else {
                    statusLabel.setText("Incorrect price \"from\".");
                }
            } catch (NumberFormatException e) {
                statusLabel.setText("Incorrect price \"from\".");
            }
        }

        if (!priceTo.getText().isEmpty()) {
            try {
                double price = Double.parseDouble(priceTo.getText());
                if (price >= 0) {
                    fl.setPriceTill(price);
                } else {
                    statusLabel.setText("Incorrect price \"to\".");
                }
            } catch (NumberFormatException e) {
                statusLabel.setText("Incorrect price \"to\".");
            }
        }
        if (!amountFrom.getText().isEmpty()) {
            try {
                double price = Double.parseDouble(amountFrom.getText());
                if (price >= 0) {
                    fl.setAmountFrom(price);
                } else {
                    statusLabel.setText("Incorrect price \"from\".");
                }
            } catch (NumberFormatException e) {
                statusLabel.setText("Incorrect price \"from\".");
            }
        }

        if (!amountTo.getText().isEmpty()) {
            try {
                double price = Double.parseDouble(amountTo.getText());
                if (price >= 0) {
                    fl.setAmountTill(price);
                } else {
                    statusLabel.setText("Incorrect price \"to\".");
                }
            } catch (NumberFormatException e) {
                statusLabel.setText("Incorrect price \"to\".");
            }
        }

        if (!manufacturer.getText().isEmpty()) {
            fl.setManufacturer(manufacturer.getText());
        }

        if (!groupName.getText().isEmpty()) {
            fl.setGroup_name(groupName.getText());
        }
        if (!descr.getText().isEmpty()) {
            fl.setGroup_name(descr.getText());
        }

        showFilteredProducts(fl);
        name.clear();
        priceTo.clear();
        priceFrom.clear();
        amountTo.clear();
        amountFrom.clear();
        manufacturer.clear();
        groupName.clear();
    }


    @FXML
    void logOut(ActionEvent event) throws MalformedURLException {
        //LoginWindowController.logOut(deleteProductBtn);
    }



    @FXML
    void showAllProducts(ActionEvent event) throws Exception {
        statusLabel.setText("");
        resetTable();
    }

    @FXML
    void initialize() throws Exception {

        idCol.setCellValueFactory(new PropertyValueFactory<>("id_product"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descrCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        manufCol.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        groupCol.setCellValueFactory(new PropertyValueFactory<>("id_group"));

        resetTable();
    }

    private void resetTable() throws Exception {
        ProductCriteria fl = new ProductCriteria();
        showFilteredProducts(fl);
    }


    private void showFilteredProducts(ProductCriteria fl) throws Exception {
        JSONObject jsonObj = new JSONObject("{ \"productFilter\":" + fl.toJSON().toString() + "}");
        Message msg = new Message(GET_LIST_PRODUCTS.ordinal(), 1, jsonObj.toString());

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


        if (command_type == GET_LIST_PRODUCTS) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);
            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");

                List<Product> products = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    products.add(new Product(array.getJSONObject(i)));
                }
                System.out.println(Product.toJSONObject(products).toString());
                System.out.println(Product.toJSONObject(products).toString().length());
                productTable.getItems().clear();
                productTable.getItems().addAll(products);

            } catch (JSONException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to get list of products!");
            }

        } else {
            statusLabel.setText("Failed to get list of products!");
        }
    }

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
//    @FXML
//    void find1(ActionEvent event) throws Exception {
//        Message msg = new Message(GET_PRODUCT.ordinal(), 1, name.getText());
//        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
//        Generator.packetId = Generator.packetId.plus(UnsignedLong.valueOf(1));
//        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
//        Thread t1 = new Thread(client1);
//        t1.start();
//        //t1.join();
//        packet.encodePackage();
//        Packet receivedPacket = client1.sendMessage(packet);
//        int command = receivedPacket.getBMsq().getCType();
//        Warehouse.cTypes[] val = Warehouse.cTypes.values();
//        Warehouse.cTypes command_type = val[command];
//
//        if (command_type == GET_PRODUCT) {
//            String message = receivedPacket.getBMsq().getMessage();
//            JSONObject information = new JSONObject(message);
//
//            try {
//                JSONObject list = information.getJSONObject("object");
//                JSONArray array = list.getJSONArray("list");
//
//                List<Product> products = new ArrayList<>();
//
//                for (int i = 0; i < array.length(); i++) {
//                    products.add(new Product(array.getJSONObject(i)));
//                    System.out.println(products.get(i));
//                }
//
//                productTable.getItems().clear();
//                productTable.getItems().addAll(products);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                statusLabel.setText("Failed to get products!");
//            }
//        } else {
//            statusLabel.setText("Failed to get products!");
//        }
//    }
//    @FXML
//    void initialize() throws Exception {
//        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
//        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
//        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
//        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
//        manufCol.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
//        descrCol.setCellValueFactory(new PropertyValueFactory<>("description"));
//        groupCol.setCellValueFactory(new PropertyValueFactory<>("group_id"));
//        resetTable();
//    }
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
//    private void resetTable() throws Exception {
//        Message msg = new Message(GET_LIST_PRODUCTS.ordinal(), 1, "");
//        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
//        Generator.packetId = Generator.packetId.plus(UnsignedLong.valueOf(1));
//        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
//        //Packet receivedPacket = GlobalContext.clientTCP.sendPacket(packet.toPacket());
//        Thread t1 = new Thread(client1);
//        t1.start();
//        //t1.join();
//        packet.encodePackage();
//        Packet receivedPacket = client1.sendMessage(packet);
//        int command = receivedPacket.getBMsq().getCType();
//        Warehouse.cTypes[] val = Warehouse.cTypes.values();
//        Warehouse.cTypes command_type = val[command];
//
//        if (command_type == GET_LIST_PRODUCTS) {
//            String message = receivedPacket.getBMsq().getMessage();
//            JSONObject information = new JSONObject(message);
//
//            try {
//                JSONObject list = information.getJSONObject("object");
//                JSONArray array = list.getJSONArray("list");
//
//                List<Product> products = new ArrayList<>();
//
//                for (int i = 0; i < array.length(); i++) {
//                    products.add(new Product(array.getJSONObject(i)));
//                    System.out.println(products.get(i));
//                }
//
//                productTable.getItems().clear();
//                productTable.getItems().addAll(products);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                statusLabel.setText("Failed to get products!");
//            }
//        } else {
//            statusLabel.setText("Failed to get products!");
//        }
//    }
    @FXML
    private void groupsChange() throws IOException {
//        FXMLLoader loader = new FXMLLoader();
//        Stage stage = (Stage) update.getScene().getWindow();
//        URL url = new File("src/main/java/org/example/ui/groupView.fxml").toURI().toURL();
//        Parent root = FXMLLoader.load(url);
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
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
