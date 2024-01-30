package main.java.com.ldb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public boolean createStore(int userId, String storeName) {
        String query = "INSERT INTO stores (store_name, user_id) VALUES (?, ?)";
        // Assuming the store name is passed. Modify as needed.
        // Store store = new Store(userId, storeName);

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(1, storeName);
            
            int rowsInserted = preparedStatement.executeUpdate();
            
            return rowsInserted == 1; // Check if the insertion was successful
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }
}
