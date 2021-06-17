package org.example.http;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.digest.DigestUtils;
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
    private static final byte[] API_KEY_SECRET_BYTES = "my-token-secret-key-kfbjfbelfbeljfbekfblefbelafblaejfasfbjlelbfjfbblbefjefbjeklfbejbfejfb".getBytes(StandardCharsets.UTF_8);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final HttpServer server = HttpServer.create();
    public static void main(String[] args) throws IOException, SQLException {
        System.out.println(DigestUtils.md5Hex("password1"));
        System.out.println(DigestUtils.md5Hex("password2"));
        System.out.println(DigestUtils.md5Hex("password3"));
        DataBase.connect();
        CRUDstatements.create();
        ServerHttp s = new ServerHttp();
//        CRUDstatements.insertUser(new User("login4",DigestUtils.md5Hex("password1"),"admin"));
//        CRUDstatements.insertUser(new User("login5",DigestUtils.md5Hex("password2"),"buyer"));
//        CRUDstatements.insertUser(new User("login6",DigestUtils.md5Hex("password3"),""));
    }
    public ServerHttp() throws IOException, SQLException {
        server.bind(new InetSocketAddress(8888), 0);
        server.createContext("/", this::rootHandler);
        server.createContext("/api/good",this::putProductHandler).setAuthenticator(new MyAuthenticator());
        server.createContext("/login",this::handleLogin);
        //in case of server shutdown we need to restore each product, which exists in database
        ResultSet r = CRUDstatements.selectAllFromProduct();
        while (r.next())
        {
            int id = r.getInt("id");
            System.out.println(id);

            server.createContext("/api/good/"+id,exchange->
            {productsHandler(exchange,id);}).setAuthenticator(new MyAuthenticator());
        }
        server.start();
    }
    public void stop() {
        this.server.stop(1);
    }
    public void rootHandler(final HttpExchange myExchange) throws IOException {
        String method = myExchange.getRequestMethod();
        System.out.println(method);
        if (method.equals("GET")) {
            myExchange.sendResponseHeaders(200, ("Available tabs: /login, /api/good, /api/good/{id_product} ").length());
            myExchange.getResponseBody().write(("Available tabs: /login, /api/good, /api/good/{id_product} ").getBytes());
            myExchange.getResponseBody().flush();
            myExchange.getResponseBody().close();
        }else {
            myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
            myExchange.getResponseBody().write(("Not appropriate command").getBytes());
            myExchange.getResponseBody().flush();
            myExchange.getResponseBody().close();
        }
    }
    private void productsHandler(final HttpExchange myExchange,int id) throws IOException {
        System.out.println("Here");
        try (final InputStream requestBody = myExchange.getRequestBody()) {
            myExchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            String method = myExchange.getRequestMethod();
            final Product product = CRUDstatements.getProduct(id);
            if (!myExchange.getPrincipal().getRealm().equals("admin")) {
                myExchange.sendResponseHeaders(403, "No permission".length());
                myExchange.getResponseBody().write("No permission".getBytes());
                myExchange.getResponseBody().flush();
                myExchange.getResponseBody().close();
                return;
            }
            switch (method) {
                case "GET":
                    if (product != null) {
                        product.setId_product(id);
                        myExchange.sendResponseHeaders(200, OBJECT_MAPPER.writeValueAsBytes(product).length);
                        myExchange.getResponseBody().write(OBJECT_MAPPER.writeValueAsBytes(product));
                        myExchange.getResponseBody().flush();
                        myExchange.getResponseBody().close();
                    } else {
                        myExchange.sendResponseHeaders(404, ("No such product").length());
                        myExchange.getResponseBody().write(("No such product").getBytes());
                        myExchange.getResponseBody().flush();
                        myExchange.getResponseBody().close();
                    }
                    break;
                case "POST":
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
                            myExchange.getResponseBody().flush();
                            myExchange.getResponseBody().close();
                            return;
                        }

                        int amount = productReceived.getAmount();
                        if (amount > 0) {
                            product.setAmount(amount);
                        } else if (amount < 0) {
                            myExchange.sendResponseHeaders(409, ("Wrong input").length());
                            myExchange.getResponseBody().write(("Wrong input").getBytes());
                            myExchange.getResponseBody().flush();
                            myExchange.getResponseBody().close();
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
                            myExchange.getResponseBody().flush();
                            myExchange.getResponseBody().close();
                            return;
                        }

                        int updated = CRUDstatements.updateProduct(product, id);

                        if (updated > 0) {
                            myExchange.sendResponseHeaders(204, -1);
                        } else {
                            myExchange.sendResponseHeaders(404, ("Can't update product").length());
                            myExchange.getResponseBody().write(("Can't update product").getBytes());
                            myExchange.getResponseBody().flush();
                            myExchange.getResponseBody().close();

                        }
                    } else {
                        myExchange.sendResponseHeaders(404, ("No such product").length());
                        myExchange.getResponseBody().write(("No such product").getBytes());
                        myExchange.getResponseBody().flush();
                        myExchange.getResponseBody().close();
                    }
                    break;
                case "DELETE":
                    if (product != null) {
                        CRUDstatements.deleteFromProduct(id);
                        server.removeContext("/api/good/" + id);
                        myExchange.sendResponseHeaders(204, -1);
                    } else {
                        myExchange.sendResponseHeaders(404, ("No such product").length());
                        myExchange.getResponseBody().write(("No such product").getBytes());
                        myExchange.getResponseBody().flush();
                        myExchange.getResponseBody().close();
                    }

                    break;
                default:
                    myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
                    myExchange.getResponseBody().write(("Not appropriate command").getBytes());
                    myExchange.getResponseBody().flush();
                    myExchange.getResponseBody().close();
                    break;
            }
        }catch(Exception e)
        {e.printStackTrace();}
    }
    private void putProductHandler(final HttpExchange myExchange) {
        try (final InputStream requestBody = myExchange.getRequestBody()) {
            myExchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            String method = myExchange.getRequestMethod();

            if (!myExchange.getPrincipal().getRealm().equals("admin")) {
                myExchange.sendResponseHeaders(403, "No permission".length());
                myExchange.getResponseBody().write("No permission".getBytes());
                myExchange.getResponseBody().flush();
                myExchange.getResponseBody().close();
                return;
            }
           if(!myExchange.getRequestURI().toString().matches(".*[0-9].*"))
           {
               if(!(method.equals("PUT")))
               {
                   myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
                   myExchange.getResponseBody().write(("Not appropriate command").getBytes());
                   myExchange.getResponseBody().flush();
                   myExchange.getResponseBody().close();
                   return;
               }
           }
            switch (method) {
                case "PUT":
                    System.out.println(requestBody.toString());
                    final Product product = OBJECT_MAPPER.readValue(requestBody, Product.class);
                    System.out.println(product);
                    if (product != null) {

                        if (product.getAmount() >= 0 && product.getPrice() >= 0 && product.getId_group() > 0) {
                            if (CRUDstatements.getGroup(product.getId_group()) == null) {
                                myExchange.sendResponseHeaders(409, ("Can`t insert, change input").length());
                                myExchange.getResponseBody().write(("Can`t insert, change input").getBytes());
                                myExchange.getResponseBody().flush();
                                myExchange.getResponseBody().close();
                                return;
                            }
                            int id = CRUDstatements.insertProduct(product);
                            if (id == 0) {
                                myExchange.sendResponseHeaders(409, ("Can`t insert, change input").length());
                                myExchange.getResponseBody().write(("Can`t insert, change input").getBytes());
                                myExchange.getResponseBody().flush();
                                myExchange.getResponseBody().close();
                                return;
                            }
                            server.createContext("/api/good/" + id, exchange ->
                            {
                                productsHandler(exchange, id);
                            }).setAuthenticator(new MyAuthenticator());
                            myExchange.sendResponseHeaders(201, ("Successfully created product number: " + id).length());
                            myExchange.getResponseBody().write(("Successfully created product number: " + id).getBytes());
                            myExchange.getResponseBody().flush();
                            myExchange.getResponseBody().close();

                        } else {
                            myExchange.sendResponseHeaders(409, ("Wrong input").length());
                            myExchange.getResponseBody().write(("Wrong input").getBytes());
                            myExchange.getResponseBody().flush();
                            myExchange.getResponseBody().close();
                        }
                    } else {
                        myExchange.sendResponseHeaders(409, ("Wrong input").length());
                        myExchange.getResponseBody().write(("Wrong input").getBytes());
                        myExchange.getResponseBody().flush();
                        myExchange.getResponseBody().close();
                    }
                    break;
                case "GET":
                    myExchange.sendResponseHeaders(404, ("No such product").length());
                    myExchange.getResponseBody().write(("No such product").getBytes());
                    myExchange.getResponseBody().flush();
                    myExchange.getResponseBody().close();
                    break;
                case "POST":
                    myExchange.sendResponseHeaders(404, ("No such product").length());
                    myExchange.getResponseBody().write(("No such product").getBytes());
                    myExchange.getResponseBody().flush();
                    myExchange.getResponseBody().close();
                    break;
                case "DELETE":
                    myExchange.sendResponseHeaders(404, ("No such product").length());
                    myExchange.getResponseBody().write(("No such product").getBytes());
                    myExchange.getResponseBody().flush();
                    myExchange.getResponseBody().close();
                    break;
                default:
                    myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
                    myExchange.getResponseBody().write(("Not appropriate command").getBytes());
                    myExchange.getResponseBody().flush();
                    myExchange.getResponseBody().close();
                    break;
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
            myExchange.getResponseBody().flush();
            myExchange.getResponseBody().close();

        } else {
            myExchange.sendResponseHeaders(401,0);
            myExchange.getResponseBody().flush();
            myExchange.getResponseBody().close();
        }
    }else {
        myExchange.sendResponseHeaders(401, 0);
        myExchange.getResponseBody().flush();
        myExchange.getResponseBody().close();
    }
}else {
        myExchange.getRequestBody();
        myExchange.sendResponseHeaders(405, ("Not appropriate command").length());
        myExchange.getResponseBody().write(("Not appropriate command").getBytes());
        myExchange.getResponseBody().flush();
        myExchange.getResponseBody().close();
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
        return claims.getSubject();
    }
}
