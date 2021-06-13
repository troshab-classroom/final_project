package org.example;

import org.example.entities.Group;
import org.example.entities.Product;
import org.example.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
                throw new RuntimeException("Can't insert group", e);
            }
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
            throw new RuntimeException("Can't insert group", e);
        }
    }
    public static int insertProduct(final Product product){
            String query = "insert into '"+ CRUDstatements.product + "'('name_product', 'price_product', 'amount_store', 'description', 'manufacturer', 'product_id_group') values (?, ?, ?, ?, ?, ?);";
            try(final PreparedStatement insertStatement = DataBase.connection.prepareStatement(query)) {

                insertStatement.setString(1, product.getTitle());
                insertStatement.setDouble(2, product.getPrice().doubleValue());
                insertStatement.setDouble(3, product.getAmount().intValue());
                insertStatement.setString(4, product.getDescription());
                insertStatement.setString(5, product.getManufacturer());
                insertStatement.setInt(6, product.getId_group().intValue());

                insertStatement.execute();

                final ResultSet result = insertStatement.getGeneratedKeys();
                return result.getInt("last_insert_rowid()");
            } catch (SQLException e) {
                throw new RuntimeException("Can't insert product", e);
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
        String sqlQuery = "SELECT id_product FROM " + product +"WHERE name_product = ?";

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
    public User getUserByLogin(final String login) {
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
            try (final PreparedStatement preparedStatement =
                         DataBase.connection.prepareStatement("update '"+ CRUDstatements.product +"' set name_product = ?, price_product = ?, amount_store = ?, description = ?, manufacturer = ?, product_id_group = ?  where id = ?")) {
                preparedStatement.setString(1, product.getTitle());
                preparedStatement.setDouble(2, product.getPrice().doubleValue());
                preparedStatement.setDouble(3, product.getAmount().intValue());
                preparedStatement.setString(4, product.getDescription());
                preparedStatement.setString(5, product.getManufacturer());
                preparedStatement.setInt(6, product.getId_group().intValue());
                preparedStatement.setInt(7, id);
                preparedStatement.executeUpdate();
                return preparedStatement.getGeneratedKeys().getInt("last_insert_rowid()");
            } catch (SQLException e) {
                throw new RuntimeException("Can't update product", e);
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
    public static void deleteFromProduct(int id) {
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
    public static void deleteFromGroup(int id) {
        String sqlQuery = "DELETE FROM " + group + " WHERE id_group = ?";

        try {
            PreparedStatement preparedStatement = DataBase.connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            System.out.println("Deleted " + id);
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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


    public static List<Product> getByCriteria(ProductCriteria criteria){

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM product where ");
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

//        if(criteria.getGroup_name()!=null){
//            sb.append("id_group == ").append(getIdGroup(criteria.getGroup_name())).append(" and ");
//        }

        sb.append(" 1=1 ");
        try(
                Statement st = DataBase.connection.createStatement();
                ResultSet res = st.executeQuery(sb.toString());
        ){
            List<Product> products = new ArrayList<>();
            while (res.next()) {
                products.add(new Product(res.getString("name_product"),res.getDouble("price_product"),res.getInt("amount_store"),res.getInt("product_id_group")));
            }
            return products;

        }catch(SQLException e){
            throw new RuntimeException("Can't select all products",e);
        }
    }




}
