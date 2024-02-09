package main.java.com.ldb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.protocol.Resultset;

import main.java.com.ldb.model.Store;
import main.java.com.ldb.utils.DatabaseConnection;

public class StoreManager {

    private static StoreManager instance;
    private Connection connection;

    private StoreManager() {
         connection = DatabaseConnection.getConnection();
    }

    public static StoreManager getInstance() {
        if (instance == null) {
            instance = new StoreManager();
        }
        return instance;
    }

    public boolean createStore(Store newStore) {
        String query = "INSERT INTO stores (user_id, name) VALUES (?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, newStore.getUserId());
            preparedStatement.setString(2, newStore.getName());
            
            int rowsInserted = preparedStatement.executeUpdate();
            
            return rowsInserted == 1; // Check if the insertion was successful
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    public Store getStoreByUserId(int userId) {
        String query = "SELECT * FROM stores WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int storeUserId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                
                Store store = new Store(id, storeUserId, name);
                return store;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // Return null if no store is found or if there's an exception
    }

    public Store getStoreById(int storeId) {
        String query = "SELECT * FROM stores WHERE id = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, storeId);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int storeUserId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
    
                Store store = new Store(id, storeUserId, name);
                return store;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null; // Return null if no store is found or if there's an exception
    }
    
    public boolean updateStore(Store store) {
        String query = "UPDATE stores SET name = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, store.getName());
            preparedStatement.setInt(2, store.getUserId());
            preparedStatement.setInt(3, store.getId());
    
            int rowsUpdated = preparedStatement.executeUpdate();
            
            return rowsUpdated == 1; // Check if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyOwnership(int userId, int storeId) {
        String query = "SELECT COUNT(*) AS count FROM stores WHERE id = ? AND user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, storeId);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
