package org.example;

public class Main {
    public static void main(String[] args) {
        DataBase.connect();
        CRUDstatements.create();
        Group fruits = new Group("Fruits", "sweet");
        Product p = new Product("orange",1);
        CRUDstatements.insertProduct(p);
        CRUDstatements.insertGroup(fruits);
        //CRUDstatements.dropProduct();

    }
}
