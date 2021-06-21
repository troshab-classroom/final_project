package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateProduct {
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

    @FXML
    void createGroup(ActionEvent event) {
        System.out.println("lollolol");
        if(nameField.getText().isEmpty() || descrField.getText().isEmpty()) {
            statusLabel.setText("Fill out all fields before adding.");
        }
//        }else{
//            Integer id = null;
//            try{
//                id = Integer.parseInt(idField.getText());
//            }catch (NumberFormatException e){
//                statusLabel.setText("Invalid ID.");
//            }
//            if(id != null && id >= 0){
//                Group group = new Group( nameField.getText(), descrField.getText());
//                Message msg = new Message(Message.cTypes.INSERT_GROUP.ordinal() , 1, group.toJSON().toString().getBytes(StandardCharsets.UTF_8));
//
//                Packet packet = new Packet((byte) 1, UnsignedLong.valueOf(GlobalContext.packetId++), msg);
//
//                Packet receivedPacket = GlobalContext.clientTCP.sendPacket(packet.toPacket());
//
//                int command = receivedPacket.getBMsq().getcType();
//                Message.cTypes[] val = Message.cTypes.values();
//                Message.cTypes command_type = val[command];
//
//                if (command_type == INSERT_GROUP) {
//                    String message = new String(receivedPacket.getBMsq().getMessage(), StandardCharsets.UTF_8);
//                    JSONObject information = new JSONObject(message);
//                    try {
//                        statusLabel.setText(information.getString("message"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    statusLabel.setText("Can't add group.");
//                }
//            }else{
//                statusLabel.setText("Invalid ID.");
//            }
//        }
    }

    @FXML
    void initialize() {

    }
}
