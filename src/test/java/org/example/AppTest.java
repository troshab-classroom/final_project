package org.example;
import static org.junit.Assert.*;
import com.google.common.primitives.UnsignedLong;
import org.example.TCP.StoreClientTCP;
import org.example.UDP.StoreClientUDP;
import org.example.UDP.StoreServerUDP;
import org.junit.Assert;
import org.junit.Test;
//commit for branch
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppTest
{
    @Test
    public void createStatementsCheck() throws SQLException {
        DataBase.connect();
        CRUDstatements.create();
        CRUDstatements.deleteFromProductAll();
        CRUDstatements.deleteFromGroupAll();
        Group fruits = new Group("Fruits", "sweet");
        Group vegies = new Group("Vegies", "healthy");
        Product p = new Product("orange",1);
        Product p1 = new Product("potato",2);
        try {
            CRUDstatements.insertProduct(p);
            CRUDstatements.insertGroup(fruits);
            CRUDstatements.insertGroup(vegies);
            CRUDstatements.insertProduct(p1);
        }catch (Exception e){}
        //trying to add the same product
        try
        {
            CRUDstatements.insertProduct(p);
        }catch(Exception e)
        {
            System.out.println("Success");
        }
        //checking if products and groups were added
        ResultSet resGr = CRUDstatements.selectAllFromGroup();
        ResultSet resPr = CRUDstatements.selectAllFromProduct();
        ArrayList<Group> r=new ArrayList<>();
        ArrayList<Product> r2=new ArrayList<>();
        assertTrue( resGr != null);
        assertTrue( resPr != null);
        while (resGr.next()) {
                int id = resGr.getInt("id_group");
                String name = resGr.getString("name_group");
                String descr = resGr.getString("description");
                r.add(new Group(name, descr));
            }
        while (resPr.next()) {
            String name = resPr.getString("name_product");
            int descr = resPr.getInt("product_id_group");
            r2.add(new Product(name, descr));
        }
        assertTrue(r.contains(fruits));
        assertTrue(r.contains(vegies));

        assertTrue(r2.contains(p));
        assertTrue(r2.contains(p1));
    }
    @Test
    public void updateStatementsCheck() throws SQLException {
        DataBase.connect();
        CRUDstatements.create();
        Group fruits = new Group("Fruits", "sweet");
        Group vegies = new Group("Vegies", "healthy");
        Product p = new Product("orange",10, 11,"KY","fresh",1);
        Product p1 = new Product("potato",2);
        try {
            CRUDstatements.insertProduct(p);
            CRUDstatements.insertGroup(fruits);
            CRUDstatements.insertGroup(vegies);
            CRUDstatements.insertProduct(p1);
        }catch (Exception e){}
        Product pCorr = new Product("banana",1);
        CRUDstatements.updateProduct(pCorr,1);
        Group gCorr =new Group("vegetables","best");
        CRUDstatements.updateGroup(gCorr,2);
        ResultSet resGr = CRUDstatements.selectAllFromGroup();
        ResultSet resPr = CRUDstatements.selectAllFromProduct();
        ArrayList<Group> r=new ArrayList<>();
        ArrayList<Product> r2=new ArrayList<>();
        assertTrue( resGr != null);
        assertTrue( resPr != null);
        while (resGr.next()) {
            int id = resGr.getInt("id_group");
            String name = resGr.getString("name_group");
            String descr = resGr.getString("description");
            r.add(new Group(name, descr));
        }
        while (resPr.next()) {
            String name = resPr.getString("name_product");
            int descr = resPr.getInt("product_id_group");
            r2.add(new Product(name, descr));
        }
        assertTrue(r.contains(gCorr));
        assertTrue(r.contains(fruits));

        assertTrue(r2.contains(pCorr));
        assertTrue(r2.contains(p1));
        CRUDstatements.dropGroup();
        CRUDstatements.dropProduct();
    }
    @Test
    public void readStatementsCheck() throws SQLException {
        DataBase.connect();
        CRUDstatements.create();
            Group fruits = new Group("Fruits", "sweet");
            Group vegies = new Group("Vegies", "healthy");
            Group dairy = new Group("Dairy", "nutritional");
            Group porri = new Group("Porridge", "casual");

        Product p = new Product("orange",10, 11,"OP","fresh",1);
        Product p1 = new Product("potato",2);
        Product p3 = new Product("milk",10, 11,"KY","fresh",3);
        Product p4 = new Product("buckwheat",4);
        try {
        CRUDstatements.insertProduct(p);
        CRUDstatements.insertGroup(fruits);
        CRUDstatements.insertGroup(vegies);
        CRUDstatements.insertProduct(p1);
        CRUDstatements.insertProduct(p3);
        CRUDstatements.insertGroup(dairy);
        CRUDstatements.insertGroup(porri);
        CRUDstatements.insertProduct(p4);
        }catch(Exception e){}

        ResultSet resGr = CRUDstatements.selectAllFromGroup();
        ResultSet resPr = CRUDstatements.selectAllFromProduct();
        ArrayList<Group> r=new ArrayList<>();
        ArrayList<Product> r2=new ArrayList<>();
        assertTrue( resGr != null);
        assertTrue( resPr != null);
        while (resGr.next()) {
            int id = resGr.getInt("id_group");
            String name = resGr.getString("name_group");
            String descr = resGr.getString("description");
            r.add(new Group(name, descr));
        }
        while (resPr.next()) {
            String name = resPr.getString("name_product");
            int price =  resPr.getInt("price_product");
            int amount =  resPr.getInt("amount_store");
            String man = resPr.getString("manufacturer");
            String des = resPr.getString("description");
            int descr = resPr.getInt("product_id_group");
            r2.add(new Product(name,price,amount,man,des, descr));
        }
        assertTrue(r.contains(vegies));
        assertTrue(r.contains(fruits));
        assertTrue(r.contains(dairy));
        assertTrue(r.contains(porri));

        assertTrue(r2.contains(p));
        assertTrue(r2.contains(p1));
        assertTrue(r2.contains(p4));
        assertTrue(r2.contains(p3));
        CRUDstatements.dropGroup();
        CRUDstatements.dropProduct();
    }
    @Test
    public void deleteStatementsCheck() throws SQLException {
        DataBase.connect();
        CRUDstatements.create();
        Group fruits = new Group("Fruits", "sweet");
        Group vegies = new Group("Vegies", "healthy");
        Product p = new Product("orange",10, 11,"KY","fresh",1);
        Product p1 = new Product("potato",2);
        try {
            CRUDstatements.insertProduct(p);
            CRUDstatements.insertGroup(fruits);
            CRUDstatements.insertGroup(vegies);
            CRUDstatements.insertProduct(p1);
        }catch (Exception e){}
        CRUDstatements.deleteFromGroup(1);
        CRUDstatements.deleteFromProduct(1);
        ResultSet resGr = CRUDstatements.selectAllFromGroup();
        ResultSet resPr = CRUDstatements.selectAllFromProduct();
        ArrayList<Group> r=new ArrayList<>();
        ArrayList<Product> r2=new ArrayList<>();
        assertTrue( resGr != null);
        assertTrue( resPr != null);
        while (resGr.next()) {
            int id = resGr.getInt("id_group");
            String name = resGr.getString("name_group");
            String descr = resGr.getString("description");
            r.add(new Group(name, descr));
        }
        while (resPr.next()) {
            String name = resPr.getString("name_product");
            int price =  resPr.getInt("price_product");
            int amount =  resPr.getInt("amount_store");
            String man = resPr.getString("manufacturer");
            String des = resPr.getString("description");
            int descr = resPr.getInt("product_id_group");
            r2.add(new Product(name,price,amount,man,des, descr));
        }
        assertTrue(!r.contains(fruits));
        assertTrue(!r2.contains(p));

        CRUDstatements.deleteFromProductAll();
        CRUDstatements.deleteFromGroupAll();
        resGr = CRUDstatements.selectAllFromGroup();
        resPr = CRUDstatements.selectAllFromProduct();
        r=new ArrayList<>();
        r2=new ArrayList<>();
        assertTrue( resGr != null);
        assertTrue( resPr != null);
        while (resGr.next()) {
            int id = resGr.getInt("id_group");
            String name = resGr.getString("name_group");
            String descr = resGr.getString("description");
            r.add(new Group(name, descr));
        }
        while (resPr.next()) {
            String name = resPr.getString("name_product");
            int descr = resPr.getInt("product_id_group");
            r2.add(new Product(name, descr));
        }
        assertTrue(r.size() ==0);
        assertTrue(r2.size() ==0);

        CRUDstatements.dropProduct();
        CRUDstatements.dropGroup();
        try {
            resGr = CRUDstatements.selectAllFromGroup();
        }catch (Exception e)
        {
            System.out.println("Success");
        }
        try {
            resPr = CRUDstatements.selectAllFromProduct();
        }catch (Exception e)
            {
                System.out.println("Success");
            }
    }
//can be tested when server is offline
    @Test
    public void given100Clients_whenServerResponds_thenCorrect() throws Exception {
        for(int i =0;i<3;++i) {
            StoreClientTCP client1 = new StoreClientTCP("127.0.0.1", 5555);
            try {
                StoreClientTCP client2 = new StoreClientTCP("127.0.0.1", 5555);
                Thread t1 = new Thread(client1);
                t1.start();
                t1.join();
                Thread t2 = new Thread(client2);
                t2.start();
                t2.join();
                String msg12="";
                String msg22="";
                msg12 = client2.sendMessage(Generator.generate());
                msg22 = client2.sendMessage(Generator.generate());
                String msg1="";
                String msg2="";
                msg1 = client1.sendMessage(Generator.generate());
                msg2 = client1.sendMessage(Generator.generate());
                Packet end = new Packet((byte) 0, UnsignedLong.fromLongBits(7L), new Message(1, 2, "bye"));
                end.encodePackage();
                String msg = client1.sendMessage(end);
                String ms = client2.sendMessage(end);
                assertEquals(msg1, "Ok");
                assertEquals(msg2, "Ok");
                assertEquals(msg12, "Ok");
                assertEquals(msg22, "Ok");
                assertEquals(msg, "bye");
                assertEquals(ms, "bye");
            }catch(Exception e)
            {
                client1.reconnect("127.0.0.1", Generator.generate(),1);
            }

        }
    }


    @Test
    public void UDPTest() throws SocketException {
      //  StoreServerUDP server = new StoreServerUDP(3000);
      //  server.start();
     for(byte i =0;i<5;i++){
         byte finalI = i;
         byte finalI1 = i;
         new Thread(() ->{
             try{
                 StoreClientUDP client = new StoreClientUDP(finalI);
                 client.send("Hello from client " + finalI);
                 Packet pac = client.receive();
                 System.out.println("response for client " + finalI + " - " + new String(pac.getBMsq().getMessage()));
                 assertEquals(pac.getBMsq().getMessage(),"ok "+ finalI1);
             } catch (Exception e) {
             }
         }).start();
    }

}

}
