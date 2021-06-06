package org.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
                "descripton TEXT)";

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
                "descripton TEXT, " +
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
    //delete statements
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
}
