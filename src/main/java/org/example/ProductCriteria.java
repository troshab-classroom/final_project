package org.example;

import lombok.Data;

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

}
