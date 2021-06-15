package org.example.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.CRUDstatements;
import org.example.DataBase;
import org.example.entities.Group;
import org.example.entities.Product;
import org.example.entities.User;
import org.junit.runner.notification.Failure;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServerHttp {
    public static void main(String[] args) throws IOException {
        DataBase.connect();
        CRUDstatements.create();
        ServerHttp s = new ServerHttp();
        CRUDstatements.insertUser(new User("login4","password1","role1"));
        CRUDstatements.insertUser(new User("login5","password2","role2"));
        CRUDstatements.insertUser(new User("login6","password3","role3"));
    }
    private static final byte[] API_KEY_SECRET_BYTES = "my-token-secret-key-kfbjfbelfbeljfbekfblefbelafblaejfasfbjlelbfjfbblbefjefbjeklfbejbfejfb".getBytes(StandardCharsets.UTF_8);
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
            if(method.equals("GET")){
                byte[] response = "{\"status\": \"ok\"}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200,response.length);
                exchange.getResponseBody().write(response);
            }else{
                exchange.sendResponseHeaders(405,0);
            }


        //    server.createContext("/login",exchange1 -> {
            if (method.equals("POST")) {
                User user = OBJECT_MAPPER.readValue(exchange.getRequestBody(), User.class);
                User fromDb = CRUDstatements.getUserByLogin(user.getLogin());
                if(fromDb != null){
                    if(fromDb.getPassword().equals(user.getPassword())){
                        String jwt = createJWT(fromDb.getLogin());
                        getUserLoginFromJWT(jwt);
                        exchange.getResponseHeaders().set("Authorization",jwt);
                        exchange.sendResponseHeaders(200,0);

                    } else {
                        exchange.sendResponseHeaders(401,0);
                    }
                }else {
                    exchange.sendResponseHeaders(401, 0);
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }



                //    });
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




//        HttpContext context = server.createContext("/api/good",exchange ->{
//            byte[] response = "{\"status\": \"ok\"}".getBytes(StandardCharsets.UTF_8);
//            exchange.getResponseHeaders().set("Content-Type", "application/json");
//            exchange.sendResponseHeaders(200,response.length);
//            exchange.getResponseBody().write(response);
//            exchange.close();
//        });
//        context.setAuthenticator(new Authenticator(){
//            @Override
//            public Result authenticate(final HttpExchange exch){
//                String jwt = exch.getRequestHeaders().getFirst("Authorization");
//                if(jwt!=null){
//                    String login = getUserLoginFromJWT(jwt);
//                    User user = CRUDstatements.getUserByLogin(login);
//
//                    if(user!=null){
//                        return new Authenticator.Success(new HttpPrincipal(login,"admin"));
//                    }
//                }
//                return new Failure(403);
//            }
//        });

    }



    private static String createJWT(String login) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date();
        Key signingKey = new SecretKeySpec(API_KEY_SECRET_BYTES, signatureAlgorithm.getJcaName());
        return  Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ TimeUnit.HOURS.toMillis(10)))
                .setSubject(login)
                .signWith( signingKey, signatureAlgorithm)
                .claim("userName","Kate")
                .compact();

    }

    private static String getUserLoginFromJWT(String jwt) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date();
        Key signingKey = new SecretKeySpec(API_KEY_SECRET_BYTES, signatureAlgorithm.getJcaName());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Expiration: " + claims.getExpiration());
        System.out.println("username: " + claims.get("userName",String.class));
        return claims.getSubject();
    }
}
