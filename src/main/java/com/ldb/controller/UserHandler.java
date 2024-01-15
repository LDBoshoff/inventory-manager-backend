package main.java.com.ldb.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import at.favre.lib.crypto.bcrypt.BCrypt;
import main.java.com.ldb.model.User;
import main.java.com.ldb.service.UserManager;
import main.java.com.ldb.utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class UserHandler implements HttpHandler {

    private final UserManager userManager;

    public UserHandler() {
        this.userManager = UserManager.getInstance(); // Create the UserManager instance if it doesn't exist
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod(); // gets request type

        if (!requestMethod.equals("POST")) { // only POST request are allowed for this endpoint which registers and logs users in
            Response.sendResponse(exchange, 405, "Method not allowed for this endpoint. Please use HTTP POST method.");
        } else {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/api/users/register")) {
                handleRegistration(exchange);
            } else if (path.equals("/api/users/login")) {
                // handleLogin(exchange);
                Response.sendResponse(exchange, 200, "Logged in");
            } else {
                Response.sendResponse(exchange, 405, "Path does not exist.");
            }
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

        // Later in your code, you can access the stored values by their field names:
        String firstName = fieldValues.get("firstName");
        String lastName = fieldValues.get("lastName");
        String email = fieldValues.get("email");
        String password = fieldValues.get("password");

        System.out.println("firstName: " + firstName + "lastName: " + lastName + "email: " + email + "password: " + password);

        // Check for unique email
        if (!userManager.uniqueEmail(email)) {
            Response.sendResponse(exchange, 409, "Email is not unique");
            return;
        }

        // Attempt to add the user to the database
        if (userManager.addUser(email, password, firstName, lastName)) {
            Response.sendResponse(exchange, 200, "User registered successfully");
        } else {
            Response.sendResponse(exchange, 500, "Failed to register user.");
        }        
    }

    // // Helper method to check if a string is null or empty
    // private boolean isNullOrEmpty(String str) {
    //     return str == null || str.trim().isEmpty();
    // }

    // // Helper method to check if the request body is empty
    // private boolean emptyRequest(InputStream inputStream) {
    //     // return inputStream == null || inputStream.available() <= 2;
    //     return inputStream == null;
    // }

    // // Helper method to parse the JSON request
    // private JSONObject parseJsonRequest(HttpExchange exchange) throws IOException {
    //     try {
    //         return new JSONObject(new JSONTokener(exchange.getRequestBody()));
    //     } catch (JSONException e) {
    //         return null;
    //     }
    // }
  
}
