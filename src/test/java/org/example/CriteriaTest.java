package org.example;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class CriteriaTest {

    @Test
    public void shouldSelectByFilters(){

        DataBase.connect();
        CRUDstatements.create();
        CRUDstatements.deleteFromProductAll();
        CRUDstatements.deleteFromGroupAll();
        Group fruits = new Group("Fruits", "sweet");
        Group vegies = new Group("Vegies", "healthy");
        Product p1 = new Product("prod1", 450 , 34, 1);
        Product p2 = new Product("prod2",124,7,2);
        Product p3 = new Product("other",124,7,2);
        CRUDstatements.insertProduct(p1);
        CRUDstatements.insertGroup(fruits);
        CRUDstatements.insertGroup(vegies);
        CRUDstatements.insertProduct(p2);
        CRUDstatements.insertProduct(p3);
        ProductCriteria pc = new ProductCriteria();
        pc.setTitle("prod");
        List<Product> test1 = CRUDstatements.getByCriteria(pc);
        for(int i=0;i<test1.size();i++){
            assertTrue(test1.get(i).getTitle().contains("prod"));
        }

        pc = new ProductCriteria();
        pc.setPriceFrom(20.0);
        List<Product> test2 = CRUDstatements.getByCriteria(pc);
        for(int i=0;i<test2.size();i++){
            assertTrue(test2.get(i).getPrice().doubleValue()>20.0);
        }
        }

    }

