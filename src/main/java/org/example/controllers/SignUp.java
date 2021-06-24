package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.collections.ObservableList;
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
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import static org.example.entities.Warehouse.cTypes.*;

public class SignUp {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;


    @FXML
    private Label statusLabel;

    @FXML
    void createUser(ActionEvent event) throws Exception {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty() ) {
            statusLabel.setText("Fill out all fields before adding.");
        } else {
            User user = new User(loginField.getText(), passwordField.getText(),"admin");
            Message msg = new Message(ADD_USER.ordinal(), 1, user.toJSON().toString());

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

            if (command_type == ADD_USER) {
                String message = receivedPacket.getBMsq().getMessage();
                JSONObject information = new JSONObject(message);
                try {
                    statusLabel.setText(information.getString("message"));
                    if(information.getString("message").equals("User successfully added!"))
                    {
                        Stage stage = (Stage) loginField.getScene().getWindow();

                        stage.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                statusLabel.setText("Can't add user.");
            }

        }
    }

    @FXML
    void initialize() {

    }
}
