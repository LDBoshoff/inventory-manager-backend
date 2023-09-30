package main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

import org.json.JSONObject;
import org.json.JSONTokener;

public class UserHandler implements HttpHandler {
    private final String url = "jdbc:mysql://localhost:3306/portfolio_project_db";  // Database URL
    private final String user = "otheruser";                                        // MySQL username
    private final String password = "swordfish";                                    // MySQL password

    private final UserManager userManager;

    public UserHandler() {
        this.userManager = UserManager.getInstance(); // Create the UserManager instance if it doesn't exist
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 200, ""); // Respond with a 200 OK status for preflight requests
        } else if ("POST".equals(exchange.getRequestMethod())) {
            String requestURI = exchange.getRequestURI().getPath();
            if ("/api/login".equals(requestURI)) {     
                handleLogin(exchange); // user login function
           } else if ("/api/register".equals(requestURI)) {
            handleRegistration(exchange); // new account creation function
           }
        } else {
            String response = "Method not allowed";  // Return an error response for unsupported HTTP methods
            sendResponse(exchange, 405, response);
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        Connection connection = null;
        
        try {
            connection = DriverManager.getConnection(url, user, password); // Establish a connection to the database
            
            JSONObject jsonObject = new JSONObject(new JSONTokener(exchange.getRequestBody()));
            
            String email = jsonObject.getString("email");
            String password = jsonObject.getString("password");
            
            if (userManager.validDetails(connection, email, password)) {
                String jwtToken = JwtUtil.createToken(email);
                
                JSONObject responseJson = new JSONObject();
                
                responseJson.put("message", "Login Successful");
                responseJson.put("token", jwtToken);  
                // exchange.getResponseHeaders().add("Authorization", "Bearer " + jwtToken);    
                sendResponse(exchange, 200, responseJson.toString()); // Send the JSON object in the response body
            } else {
                sendResponse(exchange, 401, "Unauthorized");      // Authentication failed
            }
        } catch (SQLException e) {
            sendResponse(exchange, 500, "Internal Server Error");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle the error or log it.
                }
            }
        }   
    }

    private void handleRegistration(HttpExchange exchange) throws IOException {
        Connection connection = null;
        
        try {
            connection = DriverManager.getConnection(url, user, password); // Establish a connection to the database

            JSONObject jsonObject = new JSONObject(new JSONTokener(exchange.getRequestBody()));

            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String email = jsonObject.getString("email");
            String password = jsonObject.getString("password");

            if (!userManager.uniqueEmail(connection, email)) {
                sendResponse(exchange, 409, "Email is not unique");
            } else {
                boolean userAdded = userManager.addUser(connection, firstName, lastName, email, password);
                if (userAdded) {                 
                    sendResponse(exchange, 200, "Registration successful"); // User was successfully added to the database
                } else {
                    sendResponse(exchange, 500, "Failed to register user"); // Failed to add the user to the database
                }
            }

        } catch (SQLException e) {
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }


    // Helper method to send HTTP response
    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
        exchange.getResponseHeaders().add("Content-Type", "application/json"); // Adjust the content type as needed

        exchange.sendResponseHeaders(statusCode, responseText.length());
        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}
