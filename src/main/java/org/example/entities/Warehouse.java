package org.example.entities;
import com.google.common.util.concurrent.AtomicDouble;
import org.example.entities.Product;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
public class Warehouse {
    public enum cTypes {
        INSERT_PRODUCT,
        DELETE_PRODUCT,
        UPDATE_PRODUCT,
        GET_PRODUCT,
        GET_LIST_PRODUCTS,
        GET_PRODUCTS_STATISTICS,
        DELETE_ALL_IN_GROUP, // видалити всі продукти з даної групи
        INSERT_GROUP,
        DELETE_GROUP, // видалити всю групу і її рядочки
        UPDATE_GROUP,
        GET_GROUP,
        GET_LIST_GROUPS,
        LOGIN,
        ADD_USER
    }
    private ConcurrentHashMap<String, ArrayList<Product>> warehouse;
    Warehouse(){
        warehouse = new ConcurrentHashMap<>();
    }
    AtomicInteger getProductAmount(String productTitle){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    return new AtomicInteger(p.getAmount());
                }
            }
        }
        return new AtomicInteger(-1);
    }
    void disposeProduct(String productTitle, int amount){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    new AtomicInteger(p.getAmount()).addAndGet(-amount);
                }
            }
        }
    }
    void addProduct(String productTitle, int amount){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    new AtomicInteger(p.getAmount()).addAndGet(amount);
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
                    new AtomicDouble(p.getPrice()).set(price);
                }
            }
        }
    }

    AtomicDouble getProductPrice(String productTitle){
        for(ArrayList<Product> al : warehouse.values()){
            for(Product p : al){
                if(p.getTitle().equals(productTitle)){
                    return new AtomicDouble(p.getPrice());
                }
            }
        }
        return new AtomicDouble(-1);
    }

}
