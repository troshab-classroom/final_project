package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
//import javafx.scene.web.HTMLEditorSkin;
import javafx.stage.Stage;
import org.example.GlobalContext;
import org.example.TCP.StoreClientTCP;
import org.example.entities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static org.example.entities.Warehouse.cTypes.INSERT_PRODUCT;

public class NewProductController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    void addProduct(ActionEvent event) throws Exception {
        if(nameField.getText().isEmpty() || descrField.getText().isEmpty()||priceField.getText().isEmpty()||manufField.getText().isEmpty()||amountField.getText().isEmpty()) {
            statusLabel.setText("Fill out all fields before adding.");
        }else{
            Product prod = new Product(nameField.getText(),  Double.parseDouble(priceField.getText()), Integer.parseInt(amountField.getText()), descrField.getText(),manufField.getText(),Integer.parseInt(groupChoice.getId()));
            Message msg = new Message(INSERT_PRODUCT.ordinal(), 1, prod.toJSON().toString());

            Packet packet = new Packet((byte) 1, Generator.packetId, msg);
            Generator.packetId.plus(UnsignedLong.valueOf(1));
            StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
            //Packet receivedPacket = GlobalContext.clientTCP.sendPacket(packet.toPacket());
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
            if (command_type == INSERT_PRODUCT) {
                String message = receivedPacket.getBMsq().getMessage();
                JSONObject information = new JSONObject(message);
                try {
                    statusLabel.setText(information.getString("message"));
                    if(information.getString("message").equals("Product successfully added!"))
                    {
                        Stage stage = (Stage) descrField.getScene().getWindow();

                        stage.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                statusLabel.setText("Can't add product.");
            }
        }
    }





//    @FXML
//    void saveNewProduct(ActionEvent event) throws Exception {
//        if(nameField.getText().isEmpty() || descrField.getText().isEmpty() || priceField.getText().isEmpty()
//                || amountField.getText().isEmpty() || manufField.getText().isEmpty()){
//            statusLabel.setText("Fill out all fields before adding.");
//        }else{
//            Double price = null;
//            Double amount = null;
//            try{
//                price = Double.parseDouble(priceField.getText());
//            }catch(NumberFormatException e){
//                statusLabel.setText("Incorrect price.");
//            }
//            try{
//                amount = Double.parseDouble(amountField.getText());
//            }catch(NumberFormatException e){
//                statusLabel.setText("Incorrect amount.");
//            }
//            if(price != null && amount != null && price>=0 && amount>=0) {
//
//                Product product = new Product(nameField.getText(), price, amount, descrField.getText(), manufField.getText(), groupIdChoice.getValue().getId());
//                Message msg = new Message(Message.cTypes.INSERT_PRODUCT.ordinal(), 1, product.toJSON().toString();
//
//                Packet packet = new Packet((byte) 1, UnsignedLong.valueOf(GlobalContext.packetId++), msg);
//
//                Packet receivedPacket = new Packet((byte) 1, UnsignedLong.valueOf(GlobalContext.packetId++),new Message(INSERT_PRODUCT.ordinal(), 1, GlobalContext.clientTCP.sendMessage(packet)) );
//
//                int command = receivedPacket.getBMsq().getcType();
//                Message.cTypes[] val = Message.cTypes.values();
//                Message.cTypes command_type = val[command];
//
//                if (command_type == INSERT_PRODUCT) {
//                    Message message = new Message(receivedPacket.getBMsq().getMessage());
//                    JSONObject information = new JSONObject(message);
//                    try {
//                        statusLabel.setText(information.getString("message"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    statusLabel.setText("Failed to add product!");
//                }
//            }else {
//                statusLabel.setText("Price and amount should be positive.");
//            }
//        }
//    }
//
//    @FXML
//    void initialize() {
//        Message msg = new Message(GET_LIST_GROUPS.ordinal(), 1, "".getBytes(StandardCharsets.UTF_8));
//        Packet packet = new Packet((byte) 1, UnsignedLong.valueOf(GlobalContext.packetId++), msg);
//
//        Packet receivedPacket = GlobalContext.clientTCP.sendPacket(packet.toPacket());
//
//        int command = receivedPacket.getBMsq().getcType();
//        Message.cTypes[] val = Message.cTypes.values();
//        Message.cTypes command_type = val[command];
//
//
//        if (command_type == GET_LIST_GROUPS) {
//            String message = new String(receivedPacket.getBMsq().getMessage(), StandardCharsets.UTF_8);
//            JSONObject information = new JSONObject(message);
//            System.out.println("command");
//            try {
//                JSONObject list = information.getJSONObject("object");
//                JSONArray array = list.getJSONArray("list");
//
//                ObservableList<Group> groups = FXCollections.observableArrayList();
//
//                for (int i = 0; i < array.length(); i++) {
//                    groups.add(new Group(array.getJSONObject(i)));
//                }
//
//                groupIdChoice.setItems(groups);
//
//                System.out.println(groups.get(0).toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
