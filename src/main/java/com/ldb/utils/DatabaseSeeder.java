package main.java.com.ldb.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSeeder {

    public static void seedData() {
        insertUser("john.doe@gmail.com", "john123", "John", "Doe");
        seedStoreForJohn("John's Store", 1);

        int storeId = 1; // Assuming store ID is 1. Adjust accordingly based on your actual store ID.

        insertProduct(storeId, "Cruzer", 45000.00, 10);
        insertProduct(storeId, "Ringo", 48000.00, 5);
        insertProduct(storeId, "Torino", 50000.00, 8);
        insertProduct(storeId, "Beachin", 55000.00, 15);
        insertProduct(storeId, "Clubman", 60000.00, 12);
    }

    private static void insertUser(String email, String password, String firstName, String lastName) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                // Check if the product already exists
                String checkProductSQL = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkProductSQL);
                checkStmt.setString(1, email);
                checkStmt.setString(2, password);
                ResultSet rs = checkStmt.executeQuery();

                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }
                
                if (count == 0) {
                    String sqlInsertUser = "INSERT INTO users (email, password, first_name, last_name) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertUser);

                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, password);
                    preparedStatement.setString(3, firstName);
                    preparedStatement.setString(4, lastName);

                    preparedStatement.executeUpdate();
                    System.out.println("User inserted successfully.");
                    preparedStatement.close();
                } else {
                    System.out.println("User already inserted.");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void seedStoreForJohn(String storeName, int userId) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                // Check if the product already exists
                String checkProductSQL = "SELECT COUNT(*) FROM stores WHERE user_id = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkProductSQL);
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();

                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }

                if (count == 0) {
                     // Insert the store linked to John's user ID
                    String insertStoreSQL = "INSERT INTO stores (user_id, name) VALUES (?, ?)";
                    PreparedStatement insertStoreStmt = connection.prepareStatement(insertStoreSQL);
                    insertStoreStmt.setInt(1, userId); // Use the assumed user ID for John Doe
                    insertStoreStmt.setString(2, storeName);

                    insertStoreStmt.executeUpdate();
                    System.out.println("Store seeded successfully for John Doe.");
                    insertStoreStmt.close();
                } else {
                    System.out.println("Store seeding failed for John Doe.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void insertProduct(int storeId, String name, double price, int quantity) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                // Check if the product already exists
                String checkProductSQL = "SELECT COUNT(*) FROM products WHERE name = ? AND store_id = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkProductSQL);
                checkStmt.setString(1, name);
                checkStmt.setInt(2, storeId);
                ResultSet rs = checkStmt.executeQuery();

                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }

                // If the product does not exist, insert it
                if (count == 0) {
                    String insertProductSQL = "INSERT INTO products (name, price, quantity, store_id) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStmt = connection.prepareStatement(insertProductSQL);
                    
                    insertStmt.setString(1, name);
                    insertStmt.setDouble(2, price);
                    insertStmt.setInt(3, quantity);
                    insertStmt.setInt(4, storeId);
                    
                    insertStmt.executeUpdate();
                    System.out.println("Product inserted successfully: " + name);
                    insertStmt.close();
                } else {
                    System.out.println("Product already exists, skipping insert: " + name);
                }

                checkStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
