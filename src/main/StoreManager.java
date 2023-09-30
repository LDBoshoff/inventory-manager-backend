package main;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class StoreManager {
    public StoreManager() {
        // Constructor code here, if needed
    }

    // Implement this method to fetch the user's stores from the database
    public List<Store> getStoresForUser(Connection connection, int user_id) {
        List<Store> stores = new ArrayList<>();
        
        String query = "SELECT store_id, store_name FROM stores WHERE user_id = ?"; // Implement database query to fetch user's stores based on given user email address and populate the list

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, user_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int storeId = resultSet.getInt("store_id");
                String storeName = resultSet.getString("store_name");
                
                stores.add(new Store(storeId, user_id, storeName)); // Add other store properties here
            }
        } catch (SQLException e) {
            return stores; // Handle database errors
        } 

        return stores;
    }

    // add to usermanager
    public int getUserIdFromToken(HttpExchange exchange, Connection connection) { 
        Headers headers = exchange.getRequestHeaders(); // Extract the "Authorization" header from the request
        List<String> authHeaders = headers.get("Authorization");
        
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            
            if (authHeader.startsWith("Bearer ")) {  // Check if the header starts with "Bearer "
                String token = authHeader.substring("Bearer ".length()); // Extract the token part after "Bearer "
               
                // Extract email from token
                if (JwtUtil.verifyToken(token)) {     
                    String email = JwtUtil.extractEmailFromToken(token);
                    int userId = getUserIdByEmail(connection, email); 
                    
                    return userId;
                }
            }
        }   
       
        return -1;  // Return -1 if the token is not present or invalid
    }
    
    // eventually add to usermanager
    public int getUserIdByEmail(Connection connection, String email) {
        String query = "SELECT user_id FROM users WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Handle the error or log it.
        }

        return -1;  // Return -1 if the user with the given email was not found
    }

    public boolean addStore(Connection connection, int userID, String storeName) {
        String query = "INSERT INTO stores (user_id, store_name) VALUES (?, ?)";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, storeName);
            
            int rowsInserted = preparedStatement.executeUpdate();
            // Check if the insertion was successful (1 row should be inserted)
            System.out.println("num rows inserted = " + rowsInserted + "rowsInsertedrowsInserted == 1 " + (rowsInserted == 1));
            return rowsInserted == 1;
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the error or log it.
            return false;
        }
    }
}
