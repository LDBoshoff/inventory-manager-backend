package main;
import java.sql.*;

public class UserManager {
    private static UserManager instance = null;

    private UserManager() {
        // Private constructor to prevent external instantiation
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

    public boolean validDetails(Connection connection, String email, String password) {  
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

    public boolean uniqueEmail(Connection connection, String email) {
        boolean unique = false;

        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1); // Get the count from the query result
                unique = (count == 0); // Set isValid to true if count is greater than 0
            }
        } catch (SQLException e) {
            return false;
        } 

        return unique;
    }

    public boolean addUser(Connection connection, String firstName, String lastName, String email, String password) {
        String query = "INSERT INTO users (firstName, lastName, email, password) VALUES (?, ?, ?, ?)";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            
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
