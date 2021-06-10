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
    @Override
    public boolean equals(Object o)
    {
        Product t =(Product) o;
        return (title.equals(t.getTitle()) && price.doubleValue() == t.getPrice().doubleValue()&&manufacturer.equals(t.getManufacturer())
                &&description.equals(t.getDescription())&&amount.intValue() == t.getAmount().intValue()&&id_group.intValue() ==t.getId_group().intValue());
    }
    Product(String title, double price, int amount, String man, String des, int id_group){
        this.title = title;
        this.price = new AtomicDouble(price);
        this.amount = new AtomicInteger(amount);
        this.description = des;
        this.manufacturer = man;
        this.id_group = new AtomicInteger(id_group);
    }




    Product(String title, double price, int amount, int group){
        this.title = title;
        this.price = new AtomicDouble(price);
        this.amount = new AtomicInteger(amount);
        this.id_group=new AtomicInteger(group);
        this.manufacturer = "";
        this.description ="";

    }

    Product(final AtomicInteger id, String title, double price, int amount){
        this.id_group=id;
        this.title = title;
        this.price = new AtomicDouble(price);
        this.amount = new AtomicInteger(amount);
        this.description = "";
        this.manufacturer = "";
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
