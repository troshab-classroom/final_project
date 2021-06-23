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
import java.util.ResourceBundle;

import static org.example.entities.Warehouse.cTypes.*;

public class GroupView {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label statusLabel;

    @FXML
    private Button update;
    @FXML
    private Button delete;
    @FXML
    private Button create;
    @FXML
    private Tab products_tab;
    @FXML
    private TableView<Group> groupsTable;
    @FXML
    private TableColumn idCol;
    @FXML
    private TableColumn nameCo;
    @FXML
    private TableColumn desc;
    @FXML
    private TextField name;

    @FXML
    void deleteGroup(ActionEvent event) throws Exception {
        Group group = groupsTable.getSelectionModel().getSelectedItem();

        if(group != null) {
            Message msg = new Message(DELETE_GROUP.ordinal(), 1, group.getId_group()+"");
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
            if (command_type == DELETE_GROUP) {
                String message = receivedPacket.getBMsq().getMessage();
                JSONObject information = new JSONObject(message);
                try {
                    System.out.println(information.getString("message"));
                    //statusLabel.setText(information.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                statusLabel.setText("Can't delete group.");
            }
            resetTable();
        }else{
            statusLabel.setText("Choose group to delete!");
        }
    }
    @FXML
    void updateGroup(ActionEvent event) throws Exception {
        statusLabel.setText("");

        Group group = groupsTable.getSelectionModel().getSelectedItem();

        if(group != null) {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/org/example/ui/updateGroup.fxml").toURI().toURL();
            loader.setLocation(url);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update group");

            UpdateGroup controller = loader.getController();
            controller.initData(group);

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
    void find2(ActionEvent event) throws Exception {
        Message msg = new Message(GET_GROUP.ordinal(), 1, name.getText());
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

        if (command_type == GET_GROUP) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);

            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");

                List<Group> groups = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    groups.add(new Group(array.getJSONObject(i)));
                    System.out.println(groups.get(i));
                }

                groupsTable.getItems().clear();
                groupsTable.getItems().addAll(groups);

            } catch (JSONException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to get groups!");
            }
        } else {
            statusLabel.setText("Failed to get groups!");
        }
    }
    @FXML
    void initialize() throws Exception {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id_group"));
        desc.setCellValueFactory(new PropertyValueFactory<>("description"));
        nameCo.setCellValueFactory(new PropertyValueFactory<>("name"));
        resetTable();
    }
    @FXML
    void createGroup(ActionEvent event) throws Exception {
       // statusLabel.setText("");

        URL url = new File("src/main/java/org/example/ui/newGroup.fxml").toURI().toURL();
        Parent root = null;
        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Function is not available.");
        }
        Stage stage = new Stage();
        stage.setTitle("New Group");
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
        Message msg = new Message(GET_LIST_GROUPS.ordinal(), 1, "");
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

        if (command_type == GET_LIST_GROUPS) {
            String message = receivedPacket.getBMsq().getMessage();
            JSONObject information = new JSONObject(message);

            try {
                JSONObject list = information.getJSONObject("object");
                JSONArray array = list.getJSONArray("list");

                List<Group> groups = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    groups.add(new Group(array.getJSONObject(i)));
                    System.out.println(groups.get(i));
                }

                groupsTable.getItems().clear();
                groupsTable.getItems().addAll(groups);

            } catch (JSONException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to get groups!");
            }
        } else {
            statusLabel.setText("Failed to get groups!");
        }
    }
    @FXML
    private void productsChange() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = (Stage) update.getScene().getWindow();
        URL url = new File("src/main/java/org/example/ui/ProductListView.fxml").toURI().toURL();
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
