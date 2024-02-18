package main.java.com.ldb.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSeeder {

    public static void seedData() {
        insertUser("john.doe@gmail.com", "john123", "John", "Doe");
        // Additional seeding methods can be called here as needed.
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

}
