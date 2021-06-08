package org.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CRUDstatements {
    static String tableName = "product";
    static String tableName1 = "group_product";
    //create statement
    public static void create()
    {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS " +
                tableName1 +
                " (id_group INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name_group TEXT UNIQUE," +
                "description TEXT)";

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + tableName1 + " created");
            System.out.println();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        sqlQuery = "CREATE TABLE IF NOT EXISTS " +
                tableName +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name_product TEXT UNIQUE," +
                "description TEXT, " +
                "manufacturer TEXT, " +
                "amount_store INTEGER," +
                "price_product REAL," +
                "product_id_group INTEGER," +
                "FOREIGN KEY(product_id_group) REFERENCES group_product(id_group)  ON UPDATE cascade \n" +
                "      ON DELETE cascade )";

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + tableName + " created");
            System.out.println();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    public static int insertGroup(final Group group){
            String query = "insert into '"+tableName1+"' ('name_group', 'description') values ( ?, ?);";
            try (final PreparedStatement insertStatement = DataBase.connection.prepareStatement(query)) {

                insertStatement.setString(1, group.getName());
                insertStatement.setString(2, group.getDescription());

                insertStatement.execute();
                return insertStatement.getGeneratedKeys().getInt("last_insert_rowid()");
            } catch (SQLException e) {
                throw new RuntimeException("Can't insert group", e);
            }
    }

    public static int insertProduct(final Product product){
            String query = "insert into '"+ tableName+ "'('name_product', 'price_product', 'amount_store', 'description', 'manufacturer', 'product_id_group') values (?, ?, ?, ?, ?, ?);";
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
        String sqlQuery = "SELECT * FROM " + tableName;

        try {
            Statement statement  = DataBase.connection.createStatement();

            return statement.executeQuery(sqlQuery);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }
    public static int getIdGroup(String name) {
        String sqlQuery = "SELECT id_group FROM " + tableName1+"WHERE name_group = ?";

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
        String sqlQuery = "SELECT id_product FROM " + tableName+"WHERE name_product = ?";

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
    public static ResultSet selectAllFromGroup() {
        String sqlQuery = "SELECT * FROM " + tableName1;

        try {
            Statement statement  = DataBase.connection.createStatement();

            return statement.executeQuery(sqlQuery);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    //update statements
    public int updateProduct(Product product, int id){
            try (final PreparedStatement preparedStatement =
                         DataBase.connection.prepareStatement("update '"+tableName+"' set name_product = ?, price_product = ?, amount = ?, description = ?, manufacturer = ?, product_id_group = ?  where id_product = ?")) {
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
    public int updateGroup(Group group, int id){
        try (final PreparedStatement preparedStatement =
                     DataBase.connection.prepareStatement("update '"+tableName+"' set name_group = ?, description = ? where id_group = ?")) {
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
        String sqlQuery = "DELETE FROM " + tableName ;
        try {
            Statement statement = DataBase.connection.createStatement();
            statement.execute(sqlQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void deleteFromProduct(int id) {
        String sqlQuery = "DELETE FROM " + tableName + " WHERE id = ?";

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
        String sqlQuery = "DROP TABLE " + tableName;

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + tableName + " droped");
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void deleteFromGroupAll() {
        String sqlQuery = "DELETE FROM " + tableName1 ;
        try {
            Statement statement = DataBase.connection.createStatement();
            statement.execute(sqlQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void deleteFromGroup(int id) {
        String sqlQuery = "DELETE FROM " + tableName1 + " WHERE id = ?";

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
        String sqlQuery = "DROP TABLE " + tableName1;

        try {
            Statement statement = DataBase.connection.createStatement();

            statement.execute(sqlQuery);

            System.out.println("Table " + tableName1 + " droped");
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
