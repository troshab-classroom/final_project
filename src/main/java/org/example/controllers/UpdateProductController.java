package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.TCP.StoreClientTCP;
import org.example.entities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static org.example.entities.Warehouse.cTypes.GET_LIST_GROUPS;
import static org.example.entities.Warehouse.cTypes.UPDATE_PRODUCT;

public class UpdateProductController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    private Integer productID;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField amountField;

    @FXML
    private TextField descrField;

    @FXML
    private TextField manufField;

    @FXML
    private Label statusLabel;

    @FXML
    private ChoiceBox<Group> groupChoice;


    @FXML
    void updateProduct(ActionEvent event) throws Exception {
        if(nameField.getText().isEmpty() || descrField.getText().isEmpty() || priceField.getText().isEmpty()
                || amountField.getText().isEmpty() || manufField.getText().isEmpty()){
            statusLabel.setText("Fill out all fields before updateProduct.");
        }else{
            Double price = null;
            Integer amount = null;
            try{
                price = Double.parseDouble(priceField.getText());
            }catch(NumberFormatException e){
                statusLabel.setText("Incorrect price.");
            }
            try{
                amount = Integer.parseInt(amountField.getText());
            }catch(NumberFormatException e){
                statusLabel.setText("Incorrect amount.");
            }
            if(price != null && amount != null && price>=0 && amount>=0) {

                Product product = new Product(productID, nameField.getText(), price, amount, descrField.getText(), manufField.getText(), groupChoice.getValue().getId_group());
                updateProduct(product, statusLabel);
            }else {
                statusLabel.setText("Price and amount should be positive.");
            }
        }
    }

    @FXML
    void initialize() {

    }

    public void initData(Product product) throws Exception {
        productID = product.getId_group();
        nameField.setText(product.getTitle());
        Double price = product.getPrice();
        priceField.setText(price.toString());
        Integer amount = product.getAmount();
        amountField.setText(amount.toString());
        descrField.setText(product.getDescription());
        manufField.setText(product.getManufacturer());

        Message msg = new Message(GET_LIST_GROUPS.ordinal(), 1, "");
        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
        Generator.packetId =Generator.packetId.plus(UnsignedLong.valueOf(1));
        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
        Thread t1 = new Thread(client1);
        t1.start();
        t1.join();
        System.out.println(packet);
        packet.encodePackage();
        System.out.println(packet);
        Packet receivedPacket = client1.sendMessage(packet);

        int command = receivedPacket.getBMsq().getCType();
        Warehouse.cTypes[] val = Warehouse.cTypes.values();
        Warehouse.cTypes command_type = val[command];

        if (command_type == GET_LIST_GROUPS) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);
            System.out.println("command");
            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");

                ObservableList<Group> groups = FXCollections.observableArrayList();

                for (int i = 0; i < array.length(); i++) {
                    Group group = new Group(array.getJSONObject(i));
                    groups.add(group);
                    if(group.getId_group() == product.getId_group()){
                        groupChoice.setValue(group);
                    }
                }

                groupChoice.setItems(groups);

                System.out.println(groups.get(0).toString());
            } catch (JSONException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to get list of groups!");
            }
        }
    }

    static void updateProduct(Product productToUpdate, Label statusLabel) throws Exception {
        Message msg = new Message(Warehouse.cTypes.UPDATE_PRODUCT.ordinal(), 1, productToUpdate.toJSON().toString());

        Packet packet = new Packet((byte) 1, Generator.packetId, msg);
        Generator.packetId =Generator.packetId.plus(UnsignedLong.valueOf(1));
        StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
        Thread t1 = new Thread(client1);
        t1.start();
        t1.join();
        System.out.println(packet);
        packet.encodePackage();
        System.out.println(packet);
        Packet receivedPacket = client1.sendMessage(packet);

        int command = receivedPacket.getBMsq().getCType();
        Warehouse.cTypes[] val = Warehouse.cTypes.values();
        Warehouse.cTypes command_type = val[command];

        if (command_type == UPDATE_PRODUCT) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);
            try {
                statusLabel.setText(information.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Failed to update product!");
        }
    }
}
