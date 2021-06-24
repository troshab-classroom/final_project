package org.example;

import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
public class ProductCriteria {
    private String title;
    private Double priceFrom;
    private Double priceTill;
    private Double amountFrom;
    private Double amountTill;
    private String manufacturer;
    private String description;
    private String group_name;

    public ProductCriteria(String title, Double priceFrom, Double priceTill, Double amountFrom, Double amountTill,String man,String desc, String group_name) {
        this.title = title;
        this.priceFrom = priceFrom;
        this.priceTill = priceTill;
        this.amountFrom = amountFrom;
        this.amountTill = amountTill;
        this.group_name=group_name;
        this.manufacturer=man;
        this.description=desc;
    }

    public ProductCriteria() {
    }
    public JSONObject toJSON(){

        String arr;
        String manuf;
        String tit;
        String gr;
        String des;
        if(title == null || title.isEmpty()){
            tit = "null";
        }
        else{
            tit = "\""+title+"\"";
        }
        if(group_name == null || group_name.isEmpty()){
            gr = "null";
        }
        else{
            gr = "\""+group_name+"\"";
        }
        if(manufacturer == null || manufacturer.isEmpty()){
            manuf = "null";
        }
        else{
            manuf = "\""+manufacturer+"\"";
        }
        if(description == null || description.isEmpty()){
            des = "null";
        }
        else{
            des = "\""+description+"\"";
        }
        System.out.println("{"+"\"title\":"+tit+
        ", \"fromPrice\":"+ priceFrom+", \"toPrice\":"+priceTill+
        ", \"fromAmount\":"+ amountFrom+", \"toAmount\":"+amountTill+
        ", \"description\":"+ des+
        ", \"manufacturer\":"+ manuf+", \"group_name\":"+ gr+"}");

        JSONObject json = new JSONObject("{"+"\"title\":"+tit+
                ", \"fromPrice\":"+ priceFrom+", \"toPrice\":"+priceTill+
                ", \"fromAmount\":"+ amountFrom+", \"toAmount\":"+amountTill+
                ", \"description\":"+ des+
                ", \"manufacturer\":"+ manuf+", \"group_name\":"+ gr+"}");
        return json;
    }
}
