package org.example;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Data;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Product {
    private String title;
    private AtomicDouble price;
    private String manufacturer;
    private String description;
    private AtomicInteger amount;
    private AtomicInteger id_group;
    Product(String title, double price, int amount, String man, String des, int id_group){
        this.title = title;
        this.price = new AtomicDouble(price);
        this.amount = new AtomicInteger(amount);
        this.description = des;
        this.manufacturer = man;
        this.id_group = new AtomicInteger(id_group);
    }
    Product(String title){
        this.title = title;
        this.price = new AtomicDouble(0);
        this.amount = new AtomicInteger(0);
        this.description = "";
        this.manufacturer = "";
        this.id_group = new AtomicInteger(0);
    }
    Product(String title, int id_group){
        this.title = title;
        this.id_group = new AtomicInteger(id_group);
        this.price = new AtomicDouble(0);
        this.amount = new AtomicInteger(0);
        this.description = "";
        this.manufacturer = "";
    }
}
