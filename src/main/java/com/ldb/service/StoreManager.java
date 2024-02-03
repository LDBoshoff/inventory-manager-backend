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
}
