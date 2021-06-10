package org.example;

import lombok.Data;

@Data
public class ProductCriteria {
    private String title;
    private Double priceFrom;
    private Double priceTill;
    private Double amountFrom;
    private Double amountTill;
    private String group_name;

    public ProductCriteria(String title, Double priceFrom, Double priceTill, Double amountFrom, Double amountTill) {
        this.title = title;
        this.priceFrom = priceFrom;
        this.priceTill = priceTill;
        this.amountFrom = amountFrom;
        this.amountTill = amountTill;
       // this.group_name=group_name;
    }

    public ProductCriteria() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Double getPriceTill() {
        return priceTill;
    }

    public void setPriceTill(Double priceTill) {
        this.priceTill = priceTill;
    }

    public Double getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(Double amountFrom) {
        this.amountFrom = amountFrom;
    }

    public Double getAmountTill() {
        return amountTill;
    }

    public void setAmountTill(Double amountTill) {
        this.amountTill = amountTill;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
}
