package main.java.com.ldb.service;
import java.sql.*;

import main.java.com.ldb.utils.DatabaseConnection;

public class UserManager {
    private static UserManager instance = null;
    private Connection connection;

    private UserManager() {
        connection = DatabaseConnection.getConnection();
    }

    public static UserManager getInstance() {
        if (instance == null) {                    // Checks if instance is null
            synchronized (UserManager.class) {     // Synchronize for thread safety by entering a synchronized block (only one thread can enter at a time)
                if (instance == null) {            // Double check that another thread hasn't created an instance in the meantime (could have passed first check)
                    instance = new UserManager();  // Creates a single instance of UserManager if it doesn't exist
                }
            }
        }
        return instance;
    }

    public boolean validDetails(String email, String password) {  
        boolean isValid = false;

        String query = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?"; // Execute a SQL query to check if the email and password combination exists
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
              
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();   
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1); // Get the count from the query result
                isValid = (count > 0); // Set isValid to true if count is greater than 0
            }
        } catch (SQLException e) {
            return false;
        }

        return isValid;
    }

    public boolean uniqueEmail(String email) {
        boolean unique = false;

        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1); // Get the count from the query result
                unique = (count == 0); // If the count is zero the email is unique
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } 

        return unique;
    }

    public boolean addUser(String email, String password, String firstName, String lastName)  {
        String query = "INSERT INTO users (email, password, first_name, last_name) VALUES (?, ?, ?, ?)";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, firstName);
            preparedStatement.setString(4, lastName);
            
            int rowsInserted = preparedStatement.executeUpdate();
            
            return rowsInserted == 1; // Check if the insertion was successful
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    public Integer getUserIdByEmail(String email) {
        Integer userId = null;

        String query = "SELECT id FROM users WHERE email = ?"; 

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) { 
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();   

            if (resultSet.next()) {
                userId = resultSet.getInt("id");
                
            }
        } catch (Exception e) {
            return userId;
        }

        return userId;
    }
}
