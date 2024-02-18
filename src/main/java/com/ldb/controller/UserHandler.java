package main.java.com.ldb.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.java.com.ldb.model.Store;
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

            switch (path) {
                case "/api/users/register":
                    handleRegistration(exchange);
                    break;
                case "/api/users/login":
                    handleLogin(exchange);
                    break;
                default:
                    Response.sendResponse(exchange, 405, "Path does not exist.");
                    break;
            }
            
        } else {
            Response.sendResponse(exchange, 405, "Method not allowed for this endpoint. Please use HTTP POST method.");
        }

    }

    private void handleRegistration(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> fieldValues = Request.parseAndValidateFields(exchange.getRequestBody(), new String[]{"firstName", "lastName", "email", "password"});
    
            String email = fieldValues.get("email");
            if (!userManager.uniqueEmail(email)) {
                Response.sendResponse(exchange, 409, "Email is not unique");
                return;
            }
    
            User newUser = new User(fieldValues.get("firstName"), fieldValues.get("lastName"), email, fieldValues.get("password"));
            if (userManager.addUser(newUser)) {
                User retrievedUser = userManager.getUserByEmail(newUser.getEmail());
                Store newStore = new Store(retrievedUser.getId(), "Default store name");
                storeManager.createStore(newStore);
                Response.sendResponse(exchange, 200, "User registered successfully");
            } else {
                Response.sendResponse(exchange, 500, "Failed to register user.");
            }
        } catch (IllegalArgumentException e) {
            Response.sendResponse(exchange, 400, e.getMessage());
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> fieldValues = Request.parseAndValidateFields(exchange.getRequestBody(), new String[]{"email", "password"});
    
            String email = fieldValues.get("email");
            String password = fieldValues.get("password");
            if (userManager.validDetails(email, password)) {
                User retrievedUser = userManager.getUserByEmail(email); // retrieve the user 
                Store store = storeManager.getStoreByUserId(retrievedUser.getId());

                if (store == null) {
                    Response.sendResponse(exchange, 500, "{\"message\": \"Critical error: No store found for user.\"}");
                    return;
                }

                // Proceed with login as both user and store exist
                String token = JwtUtil.createToken(retrievedUser.getId()); // Create JWT with user's ID
                exchange.getResponseHeaders().set("Authorization", "Bearer " + token);  // Add JWT to the response header
    
                String responsePayload = String.format("{\"message\": \"Logged in successfully\", \"storeId\": \"%d\"}", store.getId());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                Response.sendResponse(exchange, 200, responsePayload);
            } else {
                Response.sendResponse(exchange, 401, "Incorrect Details");
            }
        } catch (IllegalArgumentException e) {
            Response.sendResponse(exchange, 400, e.getMessage());
        }
    }
  
}   