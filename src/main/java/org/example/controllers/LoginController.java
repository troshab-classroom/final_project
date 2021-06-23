package org.example.controllers;

import com.google.common.primitives.UnsignedLong;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.TCP.StoreClientTCP;
import org.example.entities.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.example.entities.Warehouse.cTypes.LOGIN;
//TODO: statusLabel,
// possibility to see password
// normal size
// sign up button

public class LoginController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    public void processLogin() throws Exception {
        //statusLabel.setText("");

        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            //statusLabel.setText("Please, enter login and password");
        } else {
            System.out.println(passwordField.getText());
            System.out.println(DigestUtils.md5Hex(passwordField.getText()));
            System.out.println(DigestUtils.md5Hex("password1"));
            User user = new User(loginField.getText(), DigestUtils.md5Hex(passwordField.getText()));
            Message msg = new Message(LOGIN.ordinal(), 1, user.toJSON().toString());

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

            boolean loggedIn = false;

            if (command_type == LOGIN) {
                String message = receivedPacket.getBMsq().getMessage();
                JSONObject information = new JSONObject(message);

                try {
                    JSONObject object = information.getJSONObject("object");
                    //GlobalContext.role = object.getString("role");
                    loggedIn = true;
                } catch (JSONException e) {
                    System.out.println(information.getString("message"));
                    //statusLabel.setText(information.getString("message"));
                }

            } else {
                //statusLabel.setText("Failed to log in.");
            }

            if (loggedIn) {
                FXMLLoader loader = new FXMLLoader();
                Stage stage = (Stage) loginField.getScene().getWindow();
                URL url = new File("src/main/java/org/example/ui/groupView.fxml").toURI().toURL();
                Parent root = FXMLLoader.load(url);
                Scene scene = new Scene(root);
                stage.setScene(scene);
            }
        }
    }

    @FXML
    void initialize() {

    }

    public static void logOut(Button button) throws MalformedURLException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = (Stage) button.getScene().getWindow();
        URL url = new File("src/main/java/client_server/client/views/login_window.fxml").toURI().toURL();
        Parent root = null;
        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);

    }

    public static void addingUser(Label statusLabel) throws MalformedURLException {
        URL url = new File("src/main/java/client_server/client/views/signUp.fxml").toURI().toURL();
        Parent root = null;
        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Function is not available.");
        }
        Stage stage = new Stage();
        stage.setTitle("New User");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
