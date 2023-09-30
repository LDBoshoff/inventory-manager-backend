package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {

    public ProductManager() {
    }

    // Retrieve all products for a specific store from the database and return them as a list
    public List<Product> getAllProductsForStore(Connection connection, int storeId) {
        String query = "SELECT * FROM products WHERE store_id = ?";
        List<Product> products = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, storeId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("id");
                String productName = resultSet.getString("product_name");
                String sku = resultSet.getString("sku");
                String description = resultSet.getString("description");
                int quantity = resultSet.getInt("quantity");

                Product product = new Product(productId, storeId, productName, sku, description, quantity);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // Add a new product
    public boolean addProduct(Connection connection, int storeId, String productName, String sku, String description, int quantity) {
        String query = "INSERT INTO products (store_id, product_name, sku, description, quantity) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, storeId);
            preparedStatement.setString(2, productName);
            preparedStatement.setString(3, sku);
            preparedStatement.setString(4, description);
            preparedStatement.setInt(5, quantity);

            int rowsInserted = preparedStatement.executeUpdate();

            return rowsInserted == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing product by product_id
    public boolean updateProduct(Connection connection, int productId, String productName, String sku, String description, int quantity) {
        String query = "UPDATE products SET product_name = ?, sku = ?, description = ?, quantity = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productName);
            preparedStatement.setString(2, sku);
            preparedStatement.setString(3, description);
            preparedStatement.setInt(4, quantity);
            preparedStatement.setInt(5, productId);

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean updateProductQuantity(Connection connection, int productId, int newQuantity) {
        // Define the SQL query to update the product quantity
        String updateQuery = "UPDATE products SET quantity = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            // Set the new quantity and product ID as parameters in the prepared statement
            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setInt(2, productId);

            // Execute the update query
            int rowsUpdated = preparedStatement.executeUpdate();

            // Check if the update was successful (at least one row updated)
            return rowsUpdated > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Read product details by product ID and returns Product object
    public Product getProduct(Connection connection, int productId) {
        String query = "SELECT * FROM products WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, productId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int storeId = resultSet.getInt("store_id");
                String productName = resultSet.getString("product_name");
                String sku = resultSet.getString("sku");
                String description = resultSet.getString("description");
                int quantity = resultSet.getInt("quantity");

                return new Product(productId, storeId, productName, sku, description, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Product not found
    }

    // Delete entire products for a store
    public boolean deleteProduct(Connection connection, int productId) {
        String query = "DELETE FROM products WHERE id = ?"; //Change to product_id

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, productId);

            int rowsDeleted = preparedStatement.executeUpdate();

            return rowsDeleted > 0; // returns true if atleast one row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete entire products for a store
    public boolean deleteAll(Connection connection, int storeId) {
        String query = "DELETE FROM products WHERE store_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, storeId);

            int rowsDeleted = preparedStatement.executeUpdate();

            return rowsDeleted > 0; // returns true if atleast one row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
