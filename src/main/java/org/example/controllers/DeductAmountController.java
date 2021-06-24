package org.example.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.entities.Product;

public class DeductAmountController {

    private Product product;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField amountField;

    @FXML
    private Label statusLabel;

    @FXML
    void deductAmount(ActionEvent event) throws Exception {
        if(amountField.getText().isEmpty()){
            statusLabel.setText("Fill out the field before deducting.");
        }else{
            Integer amount = null;
            try{
                amount = Integer.parseInt(amountField.getText());
            }catch(NumberFormatException e){
                statusLabel.setText("Incorrect amount.");
            }
            if(amount>=0 && amount!=null && amount<=product.getAmount()){

                Product productToUpdate = new Product(product.getId_product(), product.getTitle(), product.getPrice(), product.getAmount()-amount, product.getDescription(), product.getManufacturer(), product.getId_group());
                UpdateProductController.updateProduct1(productToUpdate, statusLabel);

            }else{
                statusLabel.setText("Incorrect amount.");
            }
        }
    }


    @FXML
    void initialize() {

    }

    public void initData(Product selectedItem) {
        product = selectedItem;
    }
}
