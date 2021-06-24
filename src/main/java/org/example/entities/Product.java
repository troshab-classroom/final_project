package org.example.entities;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;
@Data
public class Product {
    private int id_product;
    private String title;
    private double price;
    private String manufacturer;
    private String description;
    private int amount;
    private int id_group;
    //@Override
//    public boolean equals(Object o)
//    {
//        Product t =(Product) o;
//        return (title.equals(t.getTitle()) && price.doubleValue() == t.getPrice().doubleValue()&&manufacturer.equals(t.getManufacturer())
//                &&description.equals(t.getDescription())&&amount.intValue() == t.getAmount().intValue()&&id_group.intValue() ==t.getId_group().intValue());
//    }

    public Product(JSONObject product){
        this.id_product = product.getInt("id");
        this.title = product.getString("name");
        this.price=product.getLong("price");
        this.amount=product.getInt("amount");
        this.manufacturer=product.getString("manufacturer");
        this.description = product.getString("description");
        this.id_group=product.getInt("group_id");
    }




    public static JSONObject toJSONObject(List<Product> products) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\"list\":[");

        for (Product g : products) {
            stringBuffer.append(g.toJSON().toString() + ", ");
        }
        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length() - 1);
        stringBuffer.append("]}");

        return new JSONObject(stringBuffer.toString());
    }

    public JSONObject toJSON(){

        JSONObject json = new JSONObject("{"+"\"id\":"+id_product+", \"name\":\""+title+
                "\", \"price\":"+ price+", \"amount\":"+amount+
                ", \"description\":\""+description+"\", \"manufacturer\":\""+manufacturer+
                "\", \"group_id\":"+id_group+"}");

        return json;
    }
    public Product(String title, double price){
        this.title = title;
        this.price = price;
    }
    public Product(String title, double price, int amount, String man, String des, int id_group){
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.description = des;
        this.manufacturer = man;
        this.id_group = id_group;
    }
public Product(String title, String man,String desc){
        manufacturer = man;
        this.title=title;
        this.description = desc;
}
    public Product(String title, double price, int amount, int group){
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.id_group=group;
        this.manufacturer = "";
        this.description ="";

    }
    public Product(int id, String title, double price, int amount,String des, String man, int group){
        this.id_product = id;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.description = des;
        this.manufacturer=man;
        this.id_group=group;

    }
    public Product(final int id, String title, double price, int amount){
        this.id_group=id;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.description = "";
        this.manufacturer = "";
    }

    public Product(String title){
        this.title = title;
        this.price =0;
        this.amount =0;
        this.description = "";
        this.manufacturer = "";
        this.id_group = 0;
    }
    public Product(String title, int id_group){
        this.title = title;
        this.id_group =id_group;
        this.price = 0;
        this.amount =0;
        this.description = "";
        this.manufacturer = "";
    }
}
