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

import static org.example.entities.Warehouse.cTypes.INSERT_GROUP;

public class NewGroup {
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
    void createGroup(ActionEvent event) throws Exception {
        System.out.println("lollolol");
        if(nameField.getText().isEmpty() || descrField.getText().isEmpty()) {
            statusLabel.setText("Fill out all fields before adding.");
        }else{
                Group group = new Group(nameField.getText(), descrField.getText());
                Message msg = new Message(INSERT_GROUP.ordinal(), 1, group.toJSON().toString());

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
                if (command_type == INSERT_GROUP) {
                    String message = receivedPacket.getBMsq().getMessage();
                    JSONObject information = new JSONObject(message);
                    try {
                        statusLabel.setText(information.getString("message"));
                        if(information.getString("message").equals("Group successfully added!"))
                        {
                            Stage stage = (Stage) descrField.getScene().getWindow();

                            stage.close();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    statusLabel.setText("Can't add group.");
                }
            }
        }

    @FXML
    void initialize() {

    }
}
