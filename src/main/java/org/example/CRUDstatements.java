package org.example;

import org.example.entities.Group;
import org.example.entities.Product;
import org.example.entities.ProductStatistics;
import org.example.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CRUDstatements {
    static String product = "product";
    static String group = "group_product";
    static String user = "users";
    //create statement
    public static void create()
    {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS " +
                group +
                " (id_group INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name_group TEXT UNIQUE," +
                "description TEXT)";

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + group + " created");
            System.out.println();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        sqlQuery = "CREATE TABLE IF NOT EXISTS " +
                product +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name_product TEXT UNIQUE," +
                "description TEXT, " +
                "manufacturer TEXT, " +
                "amount_store INTEGER," +
                "price_product REAL," +
                "product_id_group INTEGER," +
                "FOREIGN KEY(product_id_group) REFERENCES " + group +"(id_group)  ON UPDATE cascade \n" +
                "      ON DELETE cascade )";

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + product + " created");
            System.out.println();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        try (final Statement statement = DataBase.connection.createStatement()) {
            statement.execute("create table if not exists '"+user +"'('id_user' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' VARCHAR(50) NOT NULL UNIQUE, 'password' VARCHAR(250) NOT NULL, 'role' text NOT NULL)");
        } catch (final SQLException e) {
            throw new RuntimeException("Can't create table", e);
        }
    }
    public static int insertGroup(final Group group){
            String query = "insert into '"+ CRUDstatements.group +"' ('name_group', 'description') values ( ?, ?);";
            try (final PreparedStatement insertStatement = DataBase.connection.prepareStatement(query)) {

                insertStatement.setString(1, group.getName());
                insertStatement.setString(2, group.getDescription());

                insertStatement.execute();
                return insertStatement.getGeneratedKeys().getInt("last_insert_rowid()");
            } catch (SQLException e) {
                return -1;
                //throw new RuntimeException("Can't insert group", e);
            }
    }
    public List<Product> getList(final int page, final int size, final ProductCriteria filter) {
//        try (final Statement statement = DataBase.connection.createStatement()) {
//            final String query = Stream.of(
//                    in("id", filter.getIds()),
//                    gte("price", filter.getPriceFrom()),
//                    lte("price", filter.getPriceTill()),
//                    manufacturer("manufacturer", filter.getManufacturer()),
//                    group("group_id", filter.getGroup_name())
//            )
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.joining(" AND "));
//
//            final String where = query.isEmpty() ? "" : " where " + query;
//            final String sql = String.format("select id, name, ROUND(price, 2) as price, ROUND(amount,3) as amount, description, manufacturer, group_id " +
//                    "from 'products' %s limit %s offset %s", where, size, page * size);
//            final ResultSet resultSet = statement.executeQuery(sql);
//
//            final List<Product> products = new ArrayList<>();
//            while (resultSet.next()) {
//                products.add(new Product(resultSet.getInt("id"),
//                        resultSet.getString("name"),
//                        resultSet.getDouble("price"),
//                        resultSet.getInt("amount"),
//                        resultSet.getString("description"),
//                        resultSet.getString("manufacturer"),
//                        resultSet.getInt("group_id")));
//            }
//            return products;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
        return null;
    }
    public static int insertUser(User user1)
    {
        String query = "insert into '"+ CRUDstatements.user +"' ('login', 'password', 'role') values ( ?, ?, ?);";
        try (final PreparedStatement insertStatement = DataBase.connection.prepareStatement(query)) {

            insertStatement.setString(1, user1.getLogin());
            insertStatement.setString(2, user1.getPassword());
            insertStatement.setString(3, user1.getRole());
            insertStatement.execute();
            return insertStatement.getGeneratedKeys().getInt("last_insert_rowid()");
        } catch (SQLException e) {
            return -1;
           // throw new RuntimeException("Can't insert user", e);
        }
    }

    public static int insertProduct(final Product product){
            String query = "insert into '"+ CRUDstatements.product + "'('name_product', 'price_product', 'amount_store', 'description', 'manufacturer', 'product_id_group') values (?, ?, ?, ?, ?, ?);";
            try(final PreparedStatement insertStatement = DataBase.connection.prepareStatement(query)) {

                insertStatement.setString(1, product.getTitle());
                insertStatement.setDouble(2, product.getPrice());
                insertStatement.setDouble(3, product.getAmount());
                insertStatement.setString(4, product.getDescription());
                insertStatement.setString(5, product.getManufacturer());
                insertStatement.setInt(6, product.getId_group());

                insertStatement.execute();

                final ResultSet result = insertStatement.getGeneratedKeys();
                return result.getInt("last_insert_rowid()");
            } catch (SQLException e) {
                return 0;
            }
    }
    public static Product getProduct(final int id){
        try(final Statement statement = DataBase.connection.createStatement()){

            final String sql = String.format("select * from 'product' where id = %s", id);
            final ResultSet resultSet = statement.executeQuery(sql);
            Product product = null;
while(resultSet.next()) {
    product = new Product(
            resultSet.getString("name_product"),
            resultSet.getDouble("price_product"),
            resultSet.getInt("amount_store"),
            resultSet.getString("manufacturer"),
            resultSet.getString("description"),
            resultSet.getInt("product_id_group"));
}
            return product;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
//            throw new RuntimeException("Can't get product", e);
        }
    }
    public static Group getGroup(final int id){
        try(final Statement statement = DataBase.connection.createStatement()){
            final String sql = String.format("select * from 'group_product' where id_group = %s", id);
            final ResultSet resultSet = statement.executeQuery(sql);
            Group group=null;
            while(resultSet.next())
                group = new Group(resultSet.getString("name_group"),resultSet.getString("description"));
            return group;
        } catch (SQLException e) {
            return null;
//            throw new RuntimeException("Can't get product", e);
        }
    }
    //read statements
    public static ResultSet selectAllFromProduct() {
        String sqlQuery = "SELECT * FROM " + product;

        try {
            Statement statement  = DataBase.connection.createStatement();
            return statement.executeQuery(sqlQuery);
        } catch (SQLException sqlException) {
        }

        return null;
    }
    public static ResultSet selectAllFromGroup() {
        String sqlQuery = "SELECT * FROM " + group;

        try {
            Statement statement  = DataBase.connection.createStatement();

            return statement.executeQuery(sqlQuery);
        } catch (SQLException sqlException) {
        }
        return null;
    }
    public static int getIdGroup(String name) {
        String sqlQuery = "SELECT id_group FROM " + group +" WHERE name_group = ?";

        try {
            PreparedStatement statement  = DataBase.connection.prepareStatement(sqlQuery);

            statement.setString(1, name);
            statement.executeUpdate();
            return statement.getGeneratedKeys().getInt("last_insert_rowid()");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return -1;
    }
    public static int getIdProduct(String name) {
        String sqlQuery = "SELECT id FROM " + product +" WHERE name_product = ?";

        try {
            PreparedStatement statement  = DataBase.connection.prepareStatement(sqlQuery);

            statement.setString(1, name);

            statement.executeUpdate();
            return statement.getGeneratedKeys().getInt("last_insert_rowid()");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return -1;
    }
    public static ResultSet getProduct(String name) {
        String sqlQuery = String.format("SELECT id FROM %s WHERE name_product like '%"+name+"%'", product);

        try {
            Statement statement  = DataBase.connection.createStatement();
            return statement.executeQuery(sqlQuery);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    public static ResultSet getGroup(String name) {
        String sqlQuery = "SELECT * FROM " +group+" WHERE name_group like '%%"+name+"%%'";
        try {
            Statement statement  = DataBase.connection.createStatement();
            return statement.executeQuery(sqlQuery);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    public static User getUserByLogin(final String login) {
        try (final PreparedStatement insertStatement = DataBase.connection.prepareStatement("select * from 'users' where login = ?")) {
            insertStatement.setString(1, login);
            final ResultSet resultSet = insertStatement.executeQuery();

            if (resultSet.next()) {
                return new User(resultSet.getInt("id_user"),resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("role"));
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException("Can't get user by login: " + login, e);
        }
    }
    //update statements
    public static int updateProduct(Product product, int id){
        System.out.println(id);
        System.out.println(product.toString());
            try (final PreparedStatement preparedStatement =
                         DataBase.connection.prepareStatement("update '"+ CRUDstatements.product +"' set name_product = ?, price_product = ?, amount_store = ?, description = ?, manufacturer = ?, product_id_group = ?  where id = ?")) {
                preparedStatement.setString(1, product.getTitle());
                preparedStatement.setDouble(2, product.getPrice());
                preparedStatement.setDouble(3, product.getAmount());
                preparedStatement.setString(4, product.getDescription());
                preparedStatement.setString(5, product.getManufacturer());
                preparedStatement.setInt(6, product.getId_group());
                preparedStatement.setInt(7, id);
                preparedStatement.executeUpdate();
                return id;
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
                //throw new RuntimeException("Can't update product", e);
            }
    }
    public static int updateGroup(Group group, int id){
        try (final PreparedStatement preparedStatement =
                     DataBase.connection.prepareStatement("update '"+ CRUDstatements.group +"' set name_group = ?, description = ? where id_group = ?")) {
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getDescription());
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
            return preparedStatement.getGeneratedKeys().getInt("last_insert_rowid()");
        } catch (SQLException e) {
            throw new RuntimeException("Can't update product", e);
        }
    }
    //delete statements
    public static void deleteFromProductAll() {
        String sqlQuery = "DELETE FROM " + product;
        try {
            Statement statement = DataBase.connection.createStatement();
            statement.execute(sqlQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static int deleteFromProduct(int id) {
        String sqlQuery = "DELETE FROM " + product + " WHERE id = ?";

        try {
            PreparedStatement preparedStatement = DataBase.connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            System.out.println("Deleted " + id);
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return id;
    }

    public static void dropProduct() {
        String sqlQuery = "DROP TABLE " + product;

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + product + " droped");
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void deleteFromGroupAll() {
        String sqlQuery = "DELETE FROM " + group;
        try {
            Statement statement = DataBase.connection.createStatement();
            statement.execute(sqlQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static int deleteFromGroup(int id) {
        String sqlQuery = "DELETE FROM " + group + " WHERE id_group = ?";
        System.out.println("here");
        System.out.println(id);
        try {
            PreparedStatement preparedStatement = DataBase.connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            System.out.println("Deleted " + id);
            System.out.println();
            return id;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public static void dropGroup() {
        String sqlQuery = "DROP TABLE " + group;

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + group + " droped");
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<ProductStatistics> getStatisticsList(int group_id) {
        try (final Statement statement = DataBase.connection.createStatement()) {

            final String sql = String.format("select id, name_product, ROUND(price_product, 2) as price_product, amount_store, COALESCE(description,\"\") AS description, " +
                    "COALESCE(manufacturer,\"\") AS manufacturer, ROUND(price_product * amount_store, 2)as total_cost" +
                    " from 'product' where product_id_group = %s", group_id);
            final ResultSet resultSet = statement.executeQuery(sql);

            final List<ProductStatistics> products = new ArrayList<>();
            while (resultSet.next()) {
                products.add(new ProductStatistics(resultSet.getInt("id"),
                        resultSet.getString("name_product"),
                        resultSet.getDouble("price_product"),
                        resultSet.getDouble("amount_store"),
                        resultSet.getString("description"),
                        resultSet.getString("manufacturer"),
                        resultSet.getDouble("total_cost")));
            }
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Product> getByCriteria(ProductCriteria criteria){
        if(criteria.getTitle()==null&&criteria.getDescription()==null&&criteria.getManufacturer()==null
                &&criteria.getAmountFrom()==null&&criteria.getAmountTill()==null
                &&criteria.getPriceTill()==null&&criteria.getPriceFrom()==null&&criteria.getGroup_name()==null) {
            StringBuilder sb = new StringBuilder("SELECT id, name_product, price_product, amount_store, " +
                    "product.description AS description, manufacturer, product_id_group FROM product INNER JOIN group_product ON product.product_id_group = group_product.id_group");
            try {
                Statement st = DataBase.connection.createStatement();
                ResultSet res = st.executeQuery(sb.toString());
                List<Product> products = new ArrayList<>();
                while (res.next()) {
                    System.out.println(res.getString("description"));
                    System.out.println(res.getString("manufacturer"));
                    products.add(new Product(res.getInt("id"),
                            res.getString("name_product"),
                            res.getDouble("price_product"),
                            res.getInt("amount_store"),
                            res.getString("description"),
                            res.getString("manufacturer"),
                            res.getInt("product_id_group")));
                    System.out.println(products.get(products.size()-1));
                }
                System.out.println(Arrays.toString(products.toArray()));
                return products;

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't do", e);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id, name_product, price_product, amount_store, " +
                "product.description FROM product INNER JOIN group_product ON product.product_id_group = group_product.id_group where ");
        if(criteria.getTitle()!=null){
            sb.append("name_product like '%").append(criteria.getTitle()).append("%' and ");
        }

        if(criteria.getPriceFrom()!=null){
            sb.append("price_product >= ").append(criteria.getPriceFrom()).append(" and ");
        }

        if(criteria.getPriceTill()!=null){
            sb.append("price_product <= ").append(criteria.getPriceTill()).append(" and ");
        }
        if(criteria.getAmountTill()!=null){
            sb.append("amount_store <= ").append(criteria.getAmountTill()).append(" and ");
        }
        if(criteria.getAmountFrom()!=null){
            sb.append("amount_store >= ").append(criteria.getAmountFrom()).append(" and ");
        }
        if(criteria.getGroup_name()!=null){
            sb.append("name_group like %'").append(criteria.getGroup_name()).append("%' and ");
        }
        if(criteria.getManufacturer()!=null){
            sb.append("manufacturer like %'").append(criteria.getManufacturer()).append("%' and ");
        }
        if(criteria.getDescription()!=null){
            sb.append("product.description like %'").append(criteria.getDescription()).append("%' and ");
        }
        sb.append(" 1=1 ");
        try{
            Statement st = DataBase.connection.createStatement();
            ResultSet res = st.executeQuery(sb.toString());
            List<Product> products = new ArrayList<>();
            while (res.next()) {
                products.add(new Product(res.getInt("id"),res.getString("name_product"),
                        res.getDouble("price_product"), res.getInt("amount_store"),
                        res.getString("description"),res.getString("manufacturer"),
                        res.getInt("product_id_group")));
            }
            System.out.println(Arrays.toString(products.toArray()));
            return products;

        }catch(SQLException e){
            return null;
           // throw new RuntimeException("Can't do",e);
        }
    }




}
