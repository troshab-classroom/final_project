package org.example;

import org.example.entities.Product;

import javax.sound.midi.SysexMessage;
import java.util.List;

public class m {
    public static void main(String args[])
{
    //launch(args);
    DataBase.connect();
    List<Product> res =CRUDstatements.getByCriteria(new ProductCriteria("chock",null,null,null,null
            ,null,null,null));
    for (Product re : res)
        System.out.println(re);
}
}
