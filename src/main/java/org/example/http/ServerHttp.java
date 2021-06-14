package org.example.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.CRUDstatements;
import org.example.DataBase;
import org.example.entities.Group;
import org.example.entities.Product;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Map;

public class ServerHttp {
    public static void main(String[] args) throws IOException {
        DataBase.connect();
        CRUDstatements.create();
        ServerHttp s = new ServerHttp();
    }
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final HttpServer server = HttpServer.create();;

    public ServerHttp() throws IOException {
        server.bind(new InetSocketAddress(8080), 0);
        //server.createContext("/", this::rootHandler).setAuthenticator(new MyAuthenticator());
        server.createContext("/api/good",this::putProductHandler);
        server.start();
    }
    public void stop() {
        this.server.stop(1);
    }
    private void putProductHandler(final HttpExchange exchange) {
        try (final InputStream requestBody = exchange.getRequestBody()) {
            exchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            String method = exchange.getRequestMethod();

//            if (!exchange.getPrincipal().getRealm().equals("admin")) {
//                exchange.sendResponseHeaders(403, "No permission".length());
//                exchange.getResponseBody().write("No permission".getBytes());
//                return;
//            }

            if (method.equals("PUT")) {
                System.out.println(requestBody.toString());
                final Product product = OBJECT_MAPPER.readValue(requestBody, Product.class);
                System.out.println(product);
                if (product != null) {

                    if (product.getAmount() >= 0 && product.getPrice() > 0 && product.getId_group() > 0) {

                        int id = CRUDstatements.insertProduct(product);
                        exchange.sendResponseHeaders(201, ("Successfully created product!" + id).length());
                        exchange.getResponseBody().write(("Successfully created product!" + id).getBytes());

                    } else {
                        exchange.sendResponseHeaders(409, ("Wrong input").length());
                        exchange.getResponseBody().write(("Wrong input").getBytes());
                    }
                } else {
                    exchange.sendResponseHeaders(409, ("Wrong input").length());
                    exchange.getResponseBody().write(("Wrong input").getBytes());
                }
            }
//            } else if (method.equals("POST")) {
//
//                final Product productReceived = OBJECT_MAPPER.readValue(requestBody, Product.class);
//
//                Product product = PRODUCT_DAO.getProduct(productReceived.getId());
//
//                if (product != null) {
//
//                    String name = productReceived.getName();
//                    if (name != null) {
//                        product.setName(name);
//                    }
//
//                    double price = productReceived.getPrice();
//                    if (price > 0) {
//                        product.setPrice(price);
//                    } else if (price < 0) {
//                        writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
//                        return;
//                    }
//
//                    double amount = productReceived.getAmount();
//                    if (amount > 0) {
//                        product.setAmount(amount);
//                    } else if (amount < 0) {
//                        writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
//                        return;
//                    }
//
//                    String description = productReceived.getDescription();
//                    if (description != null) {
//                        product.setDescription(description);
//                    }
//
//                    String manufacturer = productReceived.getManufacturer();
//                    if (manufacturer != null) {
//                        product.setManufacturer(manufacturer);
//                    }
//
//                    Integer group_id = productReceived.getGroup_id();
//                    if (group_id > 0) {
//                        product.setGroup_id(group_id);
//                    } else if (group_id < 0) {
//                        writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
//                        return;
//                    }
//
//                    int updated = PRODUCT_DAO.updateProduct(product);
//
//                    if (updated > 0) {
//                        exchange.sendResponseHeaders(204, -1);
//                    } else {
//                        writeResponse(exchange, 404, ErrorResponse.of("Can't update product"));
//                    }
//                } else {
//                    writeResponse(exchange, 404, ErrorResponse.of("No such product"));
//                }
//            } else {
//                writeResponse(exchange, 404, ErrorResponse.of("Not appropriate command"));
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
