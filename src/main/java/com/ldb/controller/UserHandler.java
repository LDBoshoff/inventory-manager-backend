package main.java.com.ldb.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.java.com.ldb.model.User;
import main.java.com.ldb.service.StoreManager;
import main.java.com.ldb.service.UserManager;
import main.java.com.ldb.utils.*;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class UserHandler implements HttpHandler {

    private final UserManager userManager;
    private final StoreManager storeManager;

    public UserHandler() {
        this.userManager = UserManager.getInstance(); // Create the UserManager instance if it doesn't exist
        this.storeManager = StoreManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod(); // gets request type

        if (exchange.getRequestMethod().equals("OPTIONS")) {  
            Response.handlePreflight(exchange);  // Handle preflight request

        } else if (requestMethod.equals("POST")) {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/api/users/register")) {
                handleRegistration(exchange);
            } else if (path.equals("/api/users/login")) {
                handleLogin(exchange);
            } else {
                Response.sendResponse(exchange, 405, "Path does not exist.");
            }
        } else {
            Response.sendResponse(exchange, 405, "Method not allowed for this endpoint. Please use HTTP POST method.");
        }

    }

    private void handleRegistration(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        
        if (Request.emptyRequest(requestBody)) {
            Response.sendResponse(exchange, 400, "Bad Request - Empty Request");
            return;
        }
        
        // Parse the JSON object
        JSONObject jsonObject = Request.parseJsonRequest(requestBody);

        if (jsonObject == null) {
            Response.sendResponse(exchange, 500, "JSON EXCEPTION");
            return;
        }
        
        String[] requiredFields = {"firstName", "lastName", "email", "password"};       
        
        Map<String, String> fieldValues = new HashMap<>(); // Map to store field names and their corresponding values
 
        for (String field : requiredFields) {           // Check for missing or empty required fields and store their values in the map
            String value = jsonObject.optString(field); // get values from json object - returns empty string for missing key or empty value
            
            if (Request.isNullOrEmpty(value)) {
                Response.sendResponse(exchange, 400, "Bad Request - Missing or empty required data in JSON request");
                return;
            }
            
            fieldValues.put(field, value); // Store the field name and its value in the map
        }

        // Store values in request
        String firstName = fieldValues.get("firstName");
        String lastName = fieldValues.get("lastName");
        String email = fieldValues.get("email");
        String password = fieldValues.get("password");

        // Check for unique email
        if (!userManager.uniqueEmail(email)) {
            Response.sendResponse(exchange, 409, "Email is not unique");
            return;
        }

        User newUser = new User(firstName, lastName, email, password);

        // Attempt to add the user to the database
        if (newUser != null && userManager.addUser(newUser)) {
            Response.sendResponse(exchange, 200, "User registered successfully");

           
        } else {
            Response.sendResponse(exchange, 500, "Failed to register user.");
        }        
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();

        if (Request.emptyRequest(requestBody)) {
            Response.sendResponse(exchange, 400, "Bad Request - Empty Request");
            return;
        }
        
        // Parse the JSON object
        JSONObject jsonObject = Request.parseJsonRequest(requestBody);

        if (jsonObject == null) {
            Response.sendResponse(exchange, 500, "JSON EXCEPTION");
            return;
        }

        String[] requiredFields = {"email", "password"};       
        
        Map<String, String> fieldValues = new HashMap<>(); // Map to store field names and their corresponding values
 
        for (String field : requiredFields) {           // Check for missing or empty required fields and store their values in the map
            String value = jsonObject.optString(field); // get values from json object - returns empty string for missing key or empty value
            
            if (Request.isNullOrEmpty(value)) {
                Response.sendResponse(exchange, 400, "Bad Request - Missing or empty required data in JSON request");
                return;
            }
            
            fieldValues.put(field, value); // Store the field name and its value in the map
        }

        // Store values in request
        String email = fieldValues.get("email");
        String password = fieldValues.get("password");

        // Confirms email & password combo
        if (userManager.validDetails(email, password)) {
            String token = JwtUtil.createToken(email);                                  // Generate a JWT with email
            exchange.getResponseHeaders().set("Authorization", "Bearer " + token);  // Add JWT to the response header
            Response.sendResponse(exchange, 200, "Logged in");
        } else {
            Response.sendResponse(exchange, 401, "Incorrect Details"); // Check that status code is correct
        }
       
    }
  
}
