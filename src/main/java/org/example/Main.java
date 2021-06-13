package org.example;

import org.example.entities.Group;
import org.example.entities.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataBase.connect();
        CRUDstatements.create();
        CRUDstatements.deleteFromProductAll();
        CRUDstatements.deleteFromGroupAll();
        Group fruits = new Group("Fruits", "sweet");
        Product p = new Product("orange",1);
        CRUDstatements.insertProduct(p);
        CRUDstatements.insertGroup(fruits);
        ResultSet r = CRUDstatements.selectAllFromGroup();
        while (r.next()) {
            int id = r.getInt("id_group");
            String name = r.getString("name_group");
            String descr = r.getString("description");
            System.out.println(id+" "+name+" "+descr);
        }

    }
}
