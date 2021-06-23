package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductStatistics {
    private Integer id;
    private String name;
    private double price;
    private double amount;
    private String description;
    private String manufacturer;
    private Double total_cost;

    public ProductStatistics(JSONObject productStatistics) {
        this.id = productStatistics.getInt("id");
        this.name = productStatistics.getString("name");
        this.price = productStatistics.getDouble("price");
        this.amount = productStatistics.getDouble("amount");
        this.description = productStatistics.getString("description");
        this.manufacturer = productStatistics.getString("manufacturer");
        this.total_cost = productStatistics.getDouble("total_cost");
    }

    public JSONObject toJSON() {

        JSONObject json = new JSONObject("{" + "\"id\":" + id + ", \"name\":\"" + name +
                "\", \"price\":" + price + ", \"amount\":" + amount +
                ", \"description\":\"" + description + "\", \"manufacturer\":\"" + manufacturer +
                "\", \"total_cost\":" + total_cost + "}");

        return json;
    }

    public static JSONObject toJSONObject(List<ProductStatistics> products){
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\"list\":[");

        for (ProductStatistics g: products) {
            stringBuffer.append(g.toJSON().toString() + ", ");
        }
        stringBuffer.delete(stringBuffer.length()-1, stringBuffer.length()-1);
        stringBuffer.append("]}");
        return new JSONObject(stringBuffer.toString());
    }

    @Override
    public String toString() {
        return "Product(" + "id=" + id + ", name=" + name + ", price=" + price +
                ", amount=" + amount + ", description=" + description +
                ", manufacturer=" + manufacturer + ", total_cost=" + total_cost + ')';
    }

}
