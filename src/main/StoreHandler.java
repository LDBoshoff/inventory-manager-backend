package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreHandler implements HttpHandler {
    private final String url = "jdbc:mysql://localhost:3306/portfolio_project_db";  // Database URL
    private final String user = "otheruser";                                        // MySQL username
    private final String password = "swordfish";                                    // MySQL password

    private final StoreManager storeManager;
    private final ProductManager productManager;

    public StoreHandler() {
        this.storeManager = new StoreManager(); // Create the UserManager instance if it doesn't exist
        this.productManager = new ProductManager();
    }

     @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 200, ""); // Respond with a 200 OK status for preflight requests
        } else if ("GET".equals(exchange.getRequestMethod())) {
            String requestURI = exchange.getRequestURI().getPath();
        
            if ("/api/stores".equals(requestURI)) {
                String queryString = exchange.getRequestURI().getQuery();
        
                // Check if there's a query parameter "store_id" in the request
                if (queryString != null && queryString.startsWith("store_id=")) {
                    // Extract the store_id from the query parameter
                    String[] queryParams = queryString.split("=");
                    if (queryParams.length == 2) {
                        int storeId = Integer.parseInt(queryParams[1]);
                        // Handle GET request for a specific store's details (products)
                        handleStoreDetails(exchange, storeId);
                    } else {
                        String response = "Invalid request";
                        sendResponse(exchange, 400, response);
                    }
                } else {
                    // Handle GET request for fetching all stores
                    handleUserStores(exchange);
                }
            } else {
                String response = "Method not allowed";  // Return an error response for unsupported HTTP methods
                sendResponse(exchange, 405, response);
            }
           
        } else if ("POST".equals(exchange.getRequestMethod())) {
            String requestURI = exchange.getRequestURI().getPath();
            
            if ("/api/stores".equals(requestURI)) {
                // Handle POST requests for creating a new store
                handleCreateStore(exchange);
            } else {
                String response = "Method not allowed";  // Return an error response for unsupported HTTP methods
                sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleStoreDetails(HttpExchange exchange, int storeId) throws IOException {
        Connection connection = null;
        List<Product> storeProducts = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url, user, password); // Establish a connection to the database

            storeProducts = productManager.getAllProductsForStore(connection, storeId); 

            JSONArray productsArray = new JSONArray();
            
            for (Product product: storeProducts) {
                JSONObject productObject = new JSONObject();
                
                productObject.put("product_id", product.getId());
                productObject.put("store_id", product.getStoreId());
                productObject.put("product_name", product.getProductName());
                productObject.put("sku", product.getSku());
                productObject.put("description", product.getDescription());
                productObject.put("quantity", product.getQuantity());
                // Add other store properties here
                productsArray.put(productObject);
            }

            String jsonResponse = productsArray.toString();

            // System.out.println("jsonResponse being sent (store's products)!" + jsonResponse);
            sendResponse(exchange, 200, jsonResponse);
            
        } catch (SQLException e) {
            // sendResponse(exchange, 500, "Internal Server Error");
            System.out.println("handleStoreDetails method: SERVER ERROR");
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

    private void handleCreateStore(HttpExchange exchange) throws IOException {
        Connection connection = null;
        
        try {
            connection = DriverManager.getConnection(url, user, password); // Establish a connection to the database
            int userId = storeManager.getUserIdFromToken(exchange, connection);
 
            if (userId == -1) {
            sendResponse(exchange, 401, "Unauthorized");
            return;
            }
            
            JSONObject jsonObject = new JSONObject(new JSONTokener(exchange.getRequestBody()));
            String storeName = jsonObject.getString("store_name");

            boolean createdStore = storeManager.addStore(connection, userId, storeName);

            if (createdStore) {
                sendResponse(exchange, 201,"Store added successfully.");
            } else {
                sendResponse(exchange, 500, "Failed to add store.");
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

    private void handleUserStores(HttpExchange exchange) throws IOException {
        Connection connection = null;
        
        try {
            connection = DriverManager.getConnection(url, user, password); // Establish a connection to the database
            int userId = storeManager.getUserIdFromToken(exchange, connection);

            if (userId == -1) {
            sendResponse(exchange, 401, "Unauthorized");
            return;
            }
            
            List<Store> stores = storeManager.getStoresForUser(connection, userId); // Retrieves list of stores associated with user

            if (!stores.isEmpty()) {           
                // Convert the list of stores to a JSON array
                // REMOVE STORE ID FROM JSON RESPONSE IN FUTURE FOR EXTRA SECURITY
                JSONArray storesArray = new JSONArray();
                for (Store store : stores) {
                    JSONObject storeObject = new JSONObject();
                    storeObject.put("store_id", store.getStoreId());
                    storeObject.put("store_name", store.getStoreName());
                    // Add other store properties here
                    storesArray.put(storeObject);
                }

                sendResponse(exchange, 200, storesArray.toString()); // User has stores and sends it as string in JSON array format
            }
            sendResponse(exchange, 204, "User has no stores.");
  

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

    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
        exchange.getResponseHeaders().add("Content-Type", "application/json"); // Adjust the content type as needed


        exchange.sendResponseHeaders(statusCode, responseText.length());
        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}