package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.entities.Product;

import java.net.URL;
import java.util.ResourceBundle;

class AddAmountController {

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
    void addAmount(ActionEvent event) throws Exception {
        if(amountField.getText().isEmpty()){
            statusLabel.setText("Fill out the field before adding.");
        }else{
            Double amount = null;
            try{
                amount = Double.parseDouble(amountField.getText());
            }catch(NumberFormatException e){
                statusLabel.setText("Incorrect amount.");
            }
            if(amount>=0 && amount!=null){

                Product productToUpdate = new Product(product.getId_product(), product.getTitle(), product.getPrice(), product.getAmount()+amount.intValue(), product.getDescription(), product.getManufacturer(), product.getId_group());
                UpdateProductController.updateProduct(productToUpdate, statusLabel);

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