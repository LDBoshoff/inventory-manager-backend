package main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProductHandler implements HttpHandler {
    private final String url = "jdbc:mysql://localhost:3306/portfolio_project_db";  // Database URL
    private final String user = "otheruser";                                        // MySQL username
    private final String password = "swordfish";                                    // MySQL password

    private final ProductManager productManager;

    public ProductHandler() {
        this.productManager = new ProductManager();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 200, ""); // Respond with a 200 OK status for preflight requests
        } else if ("PUT".equals(exchange.getRequestMethod())) {
            String requestURI = exchange.getRequestURI().getPath();
            System.out.println("PUT Request requestURI = " + requestURI);


            if (requestURI.startsWith("/api/products")) {
                // Extract the product ID from the request URI
                String[] uriSegments = requestURI.split("/");

                if (uriSegments.length == 4) {
                    int productId = Integer.parseInt(uriSegments[3]);
                    handleUpdateProductQuantity(exchange, productId);
                } else {
                    String response = "Invalid request";
                    sendResponse(exchange, 400, response);
                }
            } else {
                String response = "Method not allowed";  // Return an error response for unsupported HTTP methods
                sendResponse(exchange, 405, response);
            }
        } else if ("POST".equals(exchange.getRequestMethod())) {
            System.out.println("CALLED POST FROM STORE.JS");

            handleAddProduct(exchange);
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            System.out.println("CALLED DELETE FROM STORE.JS");
        
            // Extract the product ID from the request URI
            String requestURI = exchange.getRequestURI().getPath();
            System.out.println("reuqestURI = " + requestURI);


            if ("/api/products/delete".equals(requestURI)) {
                String queryString = exchange.getRequestURI().getQuery();
                System.out.println("queryString = "  + queryString); 
        
                // Check if there's a query parameter "store_id" in the request
                if (queryString != null && queryString.startsWith("productId=")) {
                     // Extract the store_id from the query parameter
                    String[] queryParams = queryString.split("=");
                    if (queryParams.length == 2) {
                        int productId = Integer.parseInt(queryParams[1]);
                        System.out.println("productId = " + productId);
                         
                        handleDeleteProduct(exchange, productId);
                    } else {
                        String response = "Invalid request";
                        sendResponse(exchange, 400, response);
                    }
                }
            } else {
                String response = "Invalid request";
                sendResponse(exchange, 400, response); // 400 Bad Request for other invalid requests
            }
        }
        
    }

    private void handleDeleteProduct(HttpExchange exchange, int productId) throws IOException {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, user, password);

            boolean deleted = productManager.deleteProduct(connection, productId);
        
                    if (deleted) {
                        sendResponse(exchange, 204, "Product Deleted Successfully!"); // 204 No Content for successful deletion
                    } else {
                        sendResponse(exchange, 404, "Product not found"); // 404 Not Found if the product doesn't exist
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

    private void handleAddProduct(HttpExchange exchange) throws IOException {
        Connection connection = null; // Establish a connection to the database

        try {
            connection = DriverManager.getConnection(url, user, password);
             // Parse the JSON request body to extract product data
            JSONObject jsonObject = new JSONObject(new JSONTokener(exchange.getRequestBody()));

            int storeId = jsonObject.getInt("store_id");
            String productName = jsonObject.getString("product_name");
            String sku = jsonObject.getString("sku");
            int quantity = jsonObject.getInt("quantity");

                // Validate the input (you can add more validation as needed)
            if (storeId < 0 ||productName.isEmpty() || sku.isEmpty() || quantity < 0) {
                sendResponse(exchange, 400, "Invalid product data");
                return;
            }

            String description = " ";
                // Add the new product to the database (you'll need to implement this method)
            boolean productAdded = productManager.addProduct(connection, storeId, productName, sku, description, quantity);

            if (productAdded) {
                sendResponse(exchange, 201, "Product added successfully");
            } else {
                sendResponse(exchange, 500, "Failed to add product");
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

    private void handleUpdateProductQuantity(HttpExchange exchange, int productId) throws IOException {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, user, password); // Establish a connection to the database

            JSONObject jsonObject = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
            int newQuantity = jsonObject.getInt("quantity");
            System.out.println("newQuantity = "  + newQuantity + ", productId = " + productId);

            boolean updated = productManager.updateProductQuantity(connection, productId, newQuantity);

            if (updated) {
                System.out.println("Quantity updated successfully.");
                sendResponse(exchange, 200, "Quantity updated successfully.");
            } else {
                System.out.println("Failed to update quantity.");
                sendResponse(exchange, 500, "Failed to update quantity.");
            }
        } catch (SQLException e) {
            System.out.println("Internal Server Error!!");
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
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "PUT, OPTIONS, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
        exchange.getResponseHeaders().add("Content-Type", "application/json"); // Adjust the content type as needed

        exchange.sendResponseHeaders(statusCode, responseText.length());
        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}
