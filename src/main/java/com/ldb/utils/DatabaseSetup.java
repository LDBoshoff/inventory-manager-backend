package main.java.com.ldb.utils;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {

    public static void createTables() {
        createUsersTable();
        createStoresTable();
        createProductsTable();
    }

    private static void createUsersTable() {
        // Obtain database connection
        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            try {
                // Create a statement to execute SQL queries
                Statement statement = connection.createStatement();

                // SQL query to create the 'users' table if it doesn't exist
                String sqlCreateUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "email VARCHAR(255) NOT NULL, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "first_name VARCHAR(255), " +
                        "last_name VARCHAR(255)" +
                        ")";

                // Execute the SQL query to create the table
                statement.executeUpdate(sqlCreateUsersTable);

                // Close the statement
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    // Close the connection to release resources
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void createStoresTable() {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                String sqlCreateStoresTable = "CREATE TABLE IF NOT EXISTS stores (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id INT, " +
                        "name VARCHAR(255), " +
                        "FOREIGN KEY (user_id) REFERENCES users(id)" +
                        ")";
                statement.executeUpdate(sqlCreateStoresTable);
                statement.close();
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

    private static void createProductsTable() {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                String sqlCreateProductsTable = "CREATE TABLE IF NOT EXISTS products (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255), " +
                        "price DECIMAL(10,2), " +
                        "quantity INT, " +
                        "store_id INT, " +
                        "FOREIGN KEY (store_id) REFERENCES stores(id)" +
                        ")";
                statement.executeUpdate(sqlCreateProductsTable);
                statement.close();
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
