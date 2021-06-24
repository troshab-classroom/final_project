package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Processor implements Runnable{
    private static ExecutorService service2 = Executors.newFixedThreadPool(6);
    private Packet packet;
    BlockingQueue<Packet> queue;
    BlockingQueue<Packet> queueResponse;

    public Processor(BlockingQueue<Packet> queue, BlockingQueue<Packet> queueResponse) throws InterruptedException {
        this.packet = queue.take();
        this.queue = queue;
        this.queueResponse = queueResponse;
    }

    public static void pro(BlockingQueue<Packet> queue,BlockingQueue<Packet> queueResponse) throws Exception {
        service2.submit(new Processor(queue,queueResponse));
    }

    @Override
    public void run() {
       // Packet packet = new Packet(packetFromUser);//decoding packet from USER

        Warehouse.cTypes [] val = Warehouse.cTypes.values();
        int command = packet.getBMsq().getCType();
        Warehouse.cTypes command_type = val[command];

        String message = packet.getBMsq().getMessage();

        JSONObject information;
        int success;

        JSONresponse reply = new JSONresponse();
        try {
            switch(command_type){
                case ADD_USER:
                    information = new JSONObject(message);
                    User userToAdd = new User( information.getString("login"), DigestUtils.md5Hex(information.getString("password"))
                            ,information.getString("role"));
                    success = CRUDstatements.insertUser(userToAdd);
                    if(success == -1){
                        reply.putField("This user already exists!");
                    }
                    else{
                        reply.putField("User successfully added!");
                    }
                    break;
                case LOGIN:
                    information = new JSONObject(message);
                    User userCred = new User(information.getString("login"), information.getString("password"));
                    User user = CRUDstatements.getUserByLogin(userCred.getLogin());
                    if(user != null){
                        if(userCred.getPassword().equals(user.getPassword())){
                            reply.putObject("{\"role\":\""+user.getRole()+"\"}");
                        }
                        else{
                            reply.putField("Wrong login or password!");
                        }
                    }else{
                        reply.putField("Wrong login or password!");
                    }
                    break;
                case INSERT_PRODUCT:
                    information = new JSONObject(message);
                    Product product = new Product(information.getString("name"),
                            information.getDouble("price"),information.getInt("amount"),information.getString("description"),
                            information.getString("manufacturer"),information.getInt("group_id"));
                    success = CRUDstatements.insertProduct(product);
                    if(success == -1){
                        reply.putField("Failed to add product!");
                    }
                    else{
                        reply.putField("Product successfully added!");
                    }
                    break;

                case UPDATE_PRODUCT:
                    information = new JSONObject(message);
                    Product product2 = new Product( information.getInt("id"),information.getString("name"),
                            information.getDouble("price"),information.getInt("amount"),information.getString("description"),
                            information.getString("manufacturer"),information.getInt("group_id"));
                    success = CRUDstatements.updateProduct(product2,product2.getId_product());
                    if(success == -1){
                        reply.putField("Failed to update product!");
                    }
                    else{
                        reply.putField( "Product successfully updated!");
                    }
                    break;

                case DELETE_PRODUCT:
                    int id3 = Integer.parseInt(message);
                    success = CRUDstatements.deleteFromProduct(id3);
                    if(success == -1){
                        reply.putField("Failed to delete product!");
                    }
                    else{
                        reply.putField("Product with ID " + success + " successfully deleted!");
                    }
                    break;

                case GET_PRODUCT:
                    int id4 = Integer.parseInt(message);
                    Product product4 = CRUDstatements.getProduct(id4);
                    if(product4 == null){
                        reply.putField("Invalid product ID!");
                    }
                    else{
                        reply.putObject(product4.toJSON().toString());
                    }
                    break;

                case GET_LIST_PRODUCTS:
                    information = new JSONObject(message);
                    JSONObject filtr = information.getJSONObject("productFilter");
                    ProductCriteria filter = new ProductCriteria();
                    if(!filtr.isNull("title")){
                        filter.setTitle(filtr.getString("title"));
                    }
                    if(!filtr.isNull("group_name")){
                        filter.setGroup_name(filtr.getString("group_name"));
                    }
                    if(!filtr.isNull("manufacturer")){
                        filter.setManufacturer(filtr.getString("manufacturer"));
                    }
                    if(!filtr.isNull("toPrice")){
                        filter.setPriceTill(filtr.getDouble("toPrice"));
                    }
                    if(!filtr.isNull("fromPrice")){
                        filter.setPriceFrom(filtr.getDouble("fromPrice"));
                    }
                    if(!filtr.isNull("toAmount")){
                        filter.setPriceTill(filtr.getDouble("toAmount"));
                    }
                    if(!filtr.isNull("fromAmount")){
                        filter.setPriceFrom(filtr.getDouble("fromAmount"));
                    }
                    List<Product> products = CRUDstatements.getByCriteria(filter);
                    //System.out.println(Arrays.toString(products.toArray()));
                    if(products == null){
                        reply.putField("Invalid filters!");
                    }
                    else{
                        reply.putObject(Product.toJSONObject(products).toString());
                    }
                    break;

                case GET_PRODUCTS_STATISTICS:
                    List<ProductStatistics> productStatistics = CRUDstatements.getStatisticsList(Integer.parseInt(message));

                    if(productStatistics == null){
                        reply.putField("Can't show statistics");
                    }
                    else{
                        reply.putObject(ProductStatistics.toJSONObject(productStatistics).toString());
                    }
                    break;


                case INSERT_GROUP:
                    information = new JSONObject(message);
                    Group group = new Group( information.getInt("id"),information.getString("name")
                            ,information.getString("description"));
                    success = CRUDstatements.insertGroup(group);
                    if(success == -1){
                        reply.putField("Name should be unique!");
                    }
                    else{
                        reply.putField("Group successfully added!");
                    }
                    break;
                case DELETE_GROUP:
                    int group_id = Integer.parseInt(message);
                    success = CRUDstatements.deleteFromGroup(group_id);
                    if(success == -1){
                        reply.putField("Failed to delete group!");
                    }
                    else{
                        reply.putField("Group with ID "+success + " successfully deleted!");
                    }
                    break;
                case UPDATE_GROUP:
                    information = new JSONObject(message);
                    Group group1 = new Group( information.getInt("id"),information.getString("name")
                            ,information.getString("description"));
//                Group getByName = daoGroup.getGroupByName(group1.getName());
                    success = CRUDstatements.updateGroup(group1,group1.getId_group());
                    if(success == -1){
                        reply.putField("Invalid name of group!");
                    }
                    else{
                        reply.putField("Group successfully updated!");
                    }
                    break;
                case GET_GROUP:
                    System.out.println(message);
                    ResultSet result = CRUDstatements.getGroup(message);
                    final List<Group> groupsRes = new ArrayList<>();
                    while (result.next()) {
                        groupsRes.add(new Group(result.getInt("id_group"),
                                result.getString("name_group"),
                                result.getString("description")));
                    }
                    if(groupsRes == null){
                        reply.putField("Failed to get groups!");
                    }
                    else{
                        reply.putObject(Group.toJSONObject(groupsRes).toString());
                    }
                    break;
                case GET_LIST_GROUPS:
                    ResultSet res = CRUDstatements.selectAllFromGroup();
                    final List<Group> groups = new ArrayList<>();
                    while (res.next()) {
                        groups.add(new Group(res.getInt("id_group"),
                                res.getString("name_group"),
                                res.getString("description")));
                    }
                    System.out.println(Arrays.toString(groups.toArray()));
                    if(groups == null){
                        reply.putField("Failed to get groups!");
                    }
                    else{
                        reply.putObject(Group.toJSONObject(groups).toString());
                    }
                    break;
                default:
                    reply.putField("INVALID COMMAND");
            }

            System.out.println("Message from client: " +packet.getBMsq().getMessage()
                    + "\t\t\t and with packet ID: " + packet.getBPktId());

            Message answerMessage = new Message(packet.getBMsq().getCType(), packet.getBMsq().getBUserId(), reply.toString());
            Packet answerPacket = new Packet(packet.getBSrc(), packet.getBPktId(), answerMessage);
            queueResponse.put(answerPacket);
            //Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void shutdown(){
        try{
            service2.shutdown();
            while(!service2.awaitTermination(24L, TimeUnit.HOURS)){
                System.out.println("waiting for termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Decryptor.shutdown();
        Encryptor.shutdown();
        Sender.shutdown();
        Receiver.shutdown();
    }
}
