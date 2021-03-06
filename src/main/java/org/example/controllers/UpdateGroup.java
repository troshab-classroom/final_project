package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.TCP.StoreClientTCP;
import org.example.entities.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static org.example.entities.Warehouse.cTypes.UPDATE_GROUP;

public class UpdateGroup {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField nameField;

    @FXML
    private TextField descrField;

    @FXML
    private Label statusLabel;
    int id=0;
    @FXML
    void createGroup(ActionEvent event) throws Exception {
        System.out.println("lollolol");
        if(nameField.getText().isEmpty() || descrField.getText().isEmpty()){
            statusLabel.setText("Fill out all fields before updateProduct.");
        }else{
            Group group = new Group(id, nameField.getText(), descrField.getText());
            Message msg = new Message(UPDATE_GROUP.ordinal() , 1, group.toJSON().toString());
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

            if (command_type == UPDATE_GROUP) {
                String message = receivedPacket.getBMsq().getMessage();
                JSONObject information = new JSONObject(message);
                try {
                    statusLabel.setText(information.getString("message"));
                    if(information.getString("message").equals("Group successfully updated!"))
                    {
                        Stage stage = (Stage) descrField.getScene().getWindow();

                        stage.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    statusLabel.setText("Failed to update group!");
                }
            } else {
                statusLabel.setText("Failed to update group!");
            }
        }
    }

    @FXML
    void initialize() {

    }

    public void initData(Group group){
        id=group.getId_group();
        nameField.setText(group.getName());
        descrField.setText(group.getDescription());
    }
}
