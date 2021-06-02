package org.example;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
public class Warehouse {
    public enum cTypes {
        GET_PRODUCT_AMOUNT,
        DISPOSE_PRODUCT,
        ADD_PRODUCT,
        ADD_PRODUCT_GROUP,
        ADD_PRODUCT_TITLE_TO_GROUP,
        SET_PRODUCT_PRICE,
        EXCEPTION_FROM_SERVER,
        OK
    }
    private ConcurrentHashMap<String, ArrayList<Product>> warehouse;
    Warehouse(){
        warehouse = new ConcurrentHashMap<>();
    }
    AtomicInteger getProductAmount(String productTitle){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    return p.getAmount();
                }
            }
        }
        return new AtomicInteger(-1);
    }
    void disposeProduct(String productTitle, int amount){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    p.getAmount().addAndGet(-amount);
                }
            }
        }
    }
    void addProduct(String productTitle, int amount){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    p.getAmount().addAndGet(amount);
                }
            }
        }
    }
    void addProductGroup(String groupTitle){
        warehouse.put(groupTitle, new ArrayList<>());
    }

    void addProductTitleToGroup(String groupTitle, String productTitle){
        warehouse.get(groupTitle).add(new Product(productTitle));
    }

    void setProductPrice(String productTitle, int price){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    p.getPrice().set(price);
                }
            }
        }
    }

    AtomicInteger getProductPrice(String productTitle){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    return p.getPrice();
                }
            }
        }
        return new AtomicInteger(-1);
    }

}