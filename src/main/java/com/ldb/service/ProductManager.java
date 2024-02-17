package main.java.com.ldb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.ldb.model.Product;
import main.java.com.ldb.utils.DatabaseConnection;

public class ProductManager {

    private static ProductManager instance;
    private Connection connection;

    private ProductManager() {
        connection = DatabaseConnection.getConnection();
    }

    public static ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public boolean createProduct(Product newProduct) {
        String query = "INSERT INTO products (name, price, quantity, store_id) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newProduct.getName());
            preparedStatement.setDouble(2, newProduct.getPrice());
            preparedStatement.setInt(3, newProduct.getQuantity());
            preparedStatement.setInt(4, newProduct.getStoreId());
            
            int rowsInserted = preparedStatement.executeUpdate();
            
            return rowsInserted == 1; // Check if the insertion was successful
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    public Product getProductByNameAndStoreId(String name, int storeId) {
        String query = "SELECT * FROM products WHERE name = ? AND store_id = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, storeId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                // Assuming the storeId is the same as the one we queried for, so no need to retrieve it again
                
                Product product = new Product(id, name, price, quantity, storeId);
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // Return null if no product is found or if there's an exception
    }
    
    

    public Product getProductById(int productId) {
        String query = "SELECT * FROM products WHERE id = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, productId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                int storeId = resultSet.getInt("store_id");
                
                Product product = new Product(id, name, price, quantity, storeId);
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // Return null if no product is found or if there's an exception
    }

    public List<Product> getProductsByStoreId(int storeId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE store_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, storeId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                int retrievedStoreId = resultSet.getInt("store_id");
                
                Product product = new Product(id, name, price, quantity, retrievedStoreId);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products; // Returns the list of products for the given store ID
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET name = ?, price = ?, quantity = ?, store_id = ? WHERE id = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setInt(3, product.getQuantity());
            preparedStatement.setInt(4, product.getStoreId());
            preparedStatement.setInt(5, product.getId());
    
            int rowsUpdated = preparedStatement.executeUpdate();
            
            return rowsUpdated > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE id = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, productId);
            
            int rowsDeleted = preparedStatement.executeUpdate();
            
            return rowsDeleted > 0; // Return true if the deletion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
