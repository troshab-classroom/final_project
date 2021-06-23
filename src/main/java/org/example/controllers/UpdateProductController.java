package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.TCP.StoreClientTCP;
import org.example.entities.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

import static org.example.entities.Warehouse.cTypes.UPDATE_PRODUCT;

public class UpdateProductController {

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
    private ChoiceBox<String> groupChoice;


    @FXML
       void createProduct(ActionEvent event) throws Exception {
        System.out.println("lollolol");
        if(nameField.getText().isEmpty() || descrField.getText().isEmpty()){
            statusLabel.setText("Fill out all fields before updateProduct.");
        }else{
            Product prod = new Product(nameField.getText(),  Double.parseDouble(priceField.getText()), Integer.parseInt(amountField.getText()), descrField.getText(),manufField.getText(),Integer.parseInt(groupChoice.getId()));
            Message msg = new Message(UPDATE_PRODUCT.ordinal() , 1, prod.toJSON().toString());
            Packet packet = new Packet((byte) 1, Generator.packetId, msg);
            Generator.packetId =Generator.packetId.plus(UnsignedLong.valueOf(1));
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

            if (command_type == UPDATE_PRODUCT) {
                String message = receivedPacket.getBMsq().getMessage();
                JSONObject information = new JSONObject(message);
                try {
                    statusLabel.setText(information.getString("message"));
                    if(information.getString("message").equals("Product successfully updated!"))
                    {
                        Stage stage = (Stage) descrField.getScene().getWindow();

                        stage.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    statusLabel.setText("Failed to update product!");
                }
            } else {
                statusLabel.setText("Failed to update product!");
            }
        }
    }

    @FXML
    void initialize() {

    }

    public void initData(Product prod){
        //id=group.getId_group();
        nameField.setText(prod.getTitle());
        priceField.setText(Double.toString(prod.getPrice()));
        amountField.setText(Integer.toString(prod.getAmount()));
        manufField.setText(prod.getManufacturer());
        descrField.setText(prod.getDescription());
        groupChoice.setValue(Integer.toString(prod.getId_group()));
    }
}
