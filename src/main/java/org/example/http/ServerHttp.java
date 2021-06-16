package org.example.http;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.CRUDstatements;
import org.example.DataBase;
import org.example.entities.Product;
import org.example.entities.User;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ServerHttp {
    public static void main(String[] args) throws IOException, SQLException {
        DataBase.connect();
        CRUDstatements.create();
        ServerHttp s = new ServerHttp();
//        CRUDstatements.insertUser(new User("login4","password1","admin"));
//        CRUDstatements.insertUser(new User("login5","password2","buyer"));
//        CRUDstatements.insertUser(new User("login6","password3",""));
    }
    private static final byte[] API_KEY_SECRET_BYTES = "my-token-secret-key-kfbjfbelfbeljfbekfblefbelafblaejfasfbjlelbfjfbblbefjefbjeklfbejbfejfb".getBytes(StandardCharsets.UTF_8);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final HttpServer server = HttpServer.create();


    public ServerHttp() throws IOException, SQLException {
        server.bind(new InetSocketAddress(8080), 0);
        server.createContext("/", this::rootHandler);
        server.createContext("/api/good",this::putProductHandler).setAuthenticator(new MyAuthenticator());
        server.createContext("/login",this::handleLogin);
        //in case of server shutdown we need to restore each product, which exists in database
        ResultSet r = CRUDstatements.selectAllFromProduct();
        while (r.next())
        {
            int id = r.getInt("id");
            server.createContext("/api/good/"+id,exchange->
            {productsHandler(exchange,id);}).setAuthenticator(new MyAuthenticator());
        }
        server.start();
    }
    public void stop() {
        this.server.stop(1);
    }
    private void rootHandler(final HttpExchange myExchange) throws IOException {
        String method = myExchange.getRequestMethod();
        if (method.equals("GET")) {
            myExchange.sendResponseHeaders(200, ("Available tabs: /login, /api/good, /api/good/{id_product} ").length());
            myExchange.getResponseBody().write(("Available tabs: /login, /api/good, /api/good/{id_product} ").getBytes());
        }else {
            myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
            myExchange.getResponseBody().write(("Not appropriate command").getBytes());
        }
    }
    private void productsHandler(final HttpExchange myExchange,int id) throws IOException {
        try (final InputStream requestBody = myExchange.getRequestBody()) {
            myExchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            String method = myExchange.getRequestMethod();
            final int productId = id;
            final Product product = CRUDstatements.getProduct(productId);
//            if (!exchange.getPrincipal().getRealm().equals("admin")) {
//                exchange.sendResponseHeaders(403, "No permission".length());
//                exchange.getResponseBody().write("No permission".getBytes());
//                return;
//            }
            if (method.equals("GET")) {
                if (product != null) {
                    myExchange.sendResponseHeaders(200, OBJECT_MAPPER.writeValueAsBytes(product).length);
                    myExchange.getResponseBody().write(OBJECT_MAPPER.writeValueAsBytes(product));
                } else {
                    myExchange.sendResponseHeaders(201, ("No such product").length());
                    myExchange.getResponseBody().write(("No such product").getBytes());
                }
            }
            else if (method.equals("POST")) {
                final Product productReceived = OBJECT_MAPPER.readValue(requestBody, Product.class);
                System.out.println(product);
                if (product != null) {
                    String name = productReceived.getTitle();
                    if (name != null) {
                        product.setTitle(name);
                    }

                    double price = productReceived.getPrice();
                    if (price > 0) {
                        product.setPrice(price);
                    } else if (price < 0) {
                        myExchange.sendResponseHeaders(409, ("Wrong input").length());
                        myExchange.getResponseBody().write(("Wrong input").getBytes());
                        return;
                    }

                    int amount = productReceived.getAmount();
                    if (amount > 0) {
                        product.setAmount(amount);
                    } else if (amount < 0) {
                        myExchange.sendResponseHeaders(409, ("Wrong input").length());
                        myExchange.getResponseBody().write(("Wrong input").getBytes());
                        return;
                    }

                    String description = productReceived.getDescription();
                    if (description != null) {
                        product.setDescription(description);
                    }

                    String manufacturer = productReceived.getManufacturer();
                    if (manufacturer != null) {
                        product.setManufacturer(manufacturer);
                    }

                    Integer group_id = productReceived.getId_group();
                    if (group_id > 0) {
                        product.setId_group(group_id);
                    } else if (group_id < 0) {
                        myExchange.sendResponseHeaders(409, ("Wrong input").length());
                        myExchange.getResponseBody().write(("Wrong input").getBytes());
                        return;
                    }

                    int updated = CRUDstatements.updateProduct(product,id);

                    if (updated > 0) {
                        myExchange.sendResponseHeaders(204, -1);
                    } else {
                        myExchange.sendResponseHeaders(404, ("Can't update product").length());
                        myExchange.getResponseBody().write(("Can't update product").getBytes());
                    }
                } else {
                    myExchange.sendResponseHeaders(404, ("No such product").length());
                    myExchange.getResponseBody().write(("No such product").getBytes());
                }
            } else if (method.equals("DELETE")) {
                if (product != null) {
                    CRUDstatements.deleteFromProduct(productId);
                    myExchange.sendResponseHeaders(204, -1);
                } else {
                    myExchange.sendResponseHeaders(404, ("No such product").length());
                    myExchange.getResponseBody().write(("No such product").getBytes());
                }
            }else {
            myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
            myExchange.getResponseBody().write(("Not appropriate command").getBytes());
            }
        }
    }
    private void putProductHandler(final HttpExchange myExchange) {
        try (final InputStream requestBody = myExchange.getRequestBody()) {
            myExchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            String method = myExchange.getRequestMethod();

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
                        server.createContext("/api/good/"+id,exchange->
                        {productsHandler(exchange,id);}).setAuthenticator(new MyAuthenticator());
                        myExchange.sendResponseHeaders(201, ("Successfully created product!" + id).length());
                        myExchange.getResponseBody().write(("Successfully created product!" + id).getBytes());

                    } else {
                        myExchange.sendResponseHeaders(409, ("Wrong input").length());
                        myExchange.getResponseBody().write(("Wrong input").getBytes());
                    }
                } else {
                    myExchange.sendResponseHeaders(409, ("Wrong input").length());
                    myExchange.getResponseBody().write(("Wrong input").getBytes());
                }
            }else {
            myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
            myExchange.getResponseBody().write(("Not appropriate command").getBytes());
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


private void handleLogin(final HttpExchange myExchange) throws IOException {
        try (final InputStream requestBody = myExchange.getRequestBody()) {
    myExchange.getResponseHeaders()
            .add("Content-Type", "application/json");

    String method = myExchange.getRequestMethod();
    if (method.equals("POST")) {
    User user = OBJECT_MAPPER.readValue(myExchange.getRequestBody(), User.class);
    User fromDb = CRUDstatements.getUserByLogin(user.getLogin());
    if(fromDb != null){
        if(fromDb.getPassword().equals(user.getPassword())){
            String jwt = createJWT(fromDb.getLogin());
            getUserLoginFromJWT(jwt);
            myExchange.getResponseHeaders().set("Authorization",jwt);
            myExchange.sendResponseHeaders(200,0);

        } else {
            myExchange.sendResponseHeaders(401,0);
        }
    }else {
        myExchange.sendResponseHeaders(401, 0);
    }
}else {
        myExchange.getRequestBody();
        myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
        myExchange.getResponseBody().write(("Not appropriate command").getBytes());
    }
}catch (Exception e) {
            e.printStackTrace();
        }
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

    public static String getUserLoginFromJWT(String jwt) {
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
