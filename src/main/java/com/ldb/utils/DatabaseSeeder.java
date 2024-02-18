package main.java.com.ldb.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
                String sqlInsertUser = "INSERT INTO users (email, password, first_name, last_name) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertUser);

                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, firstName);
                preparedStatement.setString(4, lastName);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("User inserted successfully.");
                } else {
                    System.out.println("User insertion failed.");
                }

                preparedStatement.close();
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
                // Insert the store linked to John's user ID
                String insertStoreSQL = "INSERT INTO stores (user_id, name) VALUES (?, ?)";
                PreparedStatement insertStoreStmt = connection.prepareStatement(insertStoreSQL);
                insertStoreStmt.setInt(1, userId); // Use the assumed user ID for John Doe
                insertStoreStmt.setString(2, storeName);

                int affectedRows = insertStoreStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Store seeded successfully for John Doe.");
                } else {
                    System.out.println("Store seeding failed for John Doe.");
                }

                insertStoreStmt.close();
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
        // Assume connection is already established
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String insertProductSQL = "INSERT INTO products (name, price, quantity, store_id) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertProductSQL);
    
                preparedStatement.setString(1, name);
                preparedStatement.setDouble(2, price);
                preparedStatement.setInt(3, quantity);
                preparedStatement.setInt(4, storeId);
    
                // Execute the insert operation
                preparedStatement.executeUpdate();
                preparedStatement.close();
                System.out.println("Product inserted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
