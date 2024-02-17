package main.java.com.ldb.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.java.com.ldb.model.Product;
import main.java.com.ldb.service.ProductManager;
import main.java.com.ldb.service.StoreManager;
import main.java.com.ldb.utils.JwtUtil;
import main.java.com.ldb.utils.Request;
import main.java.com.ldb.utils.Response;

public class ProductHandler implements HttpHandler {

    private final ProductManager productManager;
    private final StoreManager storeManager;

    public ProductHandler() {
        this.productManager = ProductManager.getInstance();
        this.storeManager = StoreManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!JwtUtil.isAuthenticated(exchange)) {
            Response.sendResponse(exchange, 401, "Unauthorized");
            return;
        }

        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        System.out.println("Request Method =  " + requestMethod + ", Path = " + path + ", Query = " + query );

        switch (requestMethod) {
            case "OPTIONS":
                Response.handlePreflight(exchange);
                break;
            case "GET":
                handleGetRequests(exchange, path, query);
                break;
            case "POST":
                handleCreateProduct(exchange, path);
                break;
            case "PUT":
                handleUpdateProduct(exchange, path, query);
                break;
            case "DELETE":
                Response.sendResponse(exchange, 200, "DELETE Successful");
                // handleDeleteProduct(exchange, path);
                break;
            default:
                Response.sendResponse(exchange, 405, "Method not allowed for this endpoint.");
                break;
        }
    }
        
    private void handleGetRequests(HttpExchange exchange, String path, String query) throws IOException {
        if (path.equals("/api/products") && query != null) {
            Map<String, String> queryParams = Request.parseQueryString(query);
            handleGetAll(exchange, queryParams);
        } else if (path.matches("/api/products/\\d+") && query == null) { // Regex to match /api/products/{productId}
            handleGetProduct(exchange, path);
        }  else {
            Response.sendResponse(exchange, 400, "Invalid request");
        }
    }
    
    private void handleGetAll(HttpExchange exchange, Map<String, String> queryParams) throws IOException {
        int storeId = validateStoreId(queryParams.get("storeId"));
        if (storeId == -1) {
            Response.sendResponse(exchange, 400, "Invalid store ID");
            return;
        }

        if (!isAuthorized(exchange, storeId)) {
            Response.sendResponse(exchange, 401, "Unauthorized");
            return;
        }

        List<Product> products = productManager.getProductsByStoreId(storeId);
        if (products.isEmpty()) {
            Response.sendResponse(exchange, 404, "No products found for the store");
        } else {
            String jsonResponse = new JSONObject().put("products", products).toString();
            Response.sendResponse(exchange, 200, jsonResponse);
        }
    }

    private void handleGetProduct(HttpExchange exchange, String path) throws IOException {
        int productId = parseProductId(path);
        Product product = productManager.getProductById(productId);
        
        if (product == null) {
            Response.sendResponse(exchange, 404, "Product not found");
            return;
        }

        if (!isAuthorized(exchange, product.getStoreId())) {
            Response.sendResponse(exchange, 401, "Unauthorized");
            return;
        }

        Response.sendResponse(exchange, 200, new JSONObject(product).toString());
    }

    public static int parseProductId(String path) {
        try {
            int productId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            return productId;
        } catch (NumberFormatException e) {
            System.out.println("Error parsing product ID: " + e.getMessage());
            return -1;
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return -1;
        }
    }
    
    private int validateStoreId(String storeIdString) throws IOException {
        try {
            return Integer.parseInt(storeIdString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isAuthorized(HttpExchange exchange, int storeId) { 
        String token = JwtUtil.extractJWTfromHeader(exchange);
        int userId = JwtUtil.extractUserIdFromToken(token);
        
        return storeManager.verifyOwnership(userId, storeId);
    }

    private void handleCreateProduct(HttpExchange exchange, String path) throws IOException {
        if (!path.equals("/api/products")) {
            Response.sendResponse(exchange, 404, "Not Found: Invalid path for creating a product.");
        }

        String[] requiredFields = new String[]{"name", "price", "quantity", "storeId"};
    
        try {
            Map<String, String> productData = Request.parseAndValidateFields(exchange.getRequestBody(), requiredFields);   // Parse and validate request body field
            Product product = Request.productFromMap(productData); // parse the data into product object
    
            // Check authorization before proceeding
            if (!isAuthorized(exchange, product.getStoreId())) {
                Response.sendResponse(exchange, 403, "Forbidden");
                return;
            }
            
            boolean isCreated = productManager.createProduct(product);
            
            if (isCreated) {
                Product retrievedProduct = productManager.getProductByNameAndStoreId(product.getName(), product.getStoreId()); 
                Response.sendResponse(exchange, 201, new JSONObject(retrievedProduct).toString());
            } else {
                Response.sendResponse(exchange, 500, "Failed to create product");
            }
        } catch (IllegalArgumentException e) {
            Response.sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            Response.sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void handleUpdateProduct(HttpExchange exchange, String path, String query) throws IOException {
        if (path.matches("/api/products/\\d+") && query == null) {
            int productId = parseProductId(path);
            Product product = productManager.getProductById(productId);

            if (product == null) {
                Response.sendResponse(exchange, 404, "Product not found.");
                return;
            }

            if (!isAuthorized(exchange, product.getStoreId())) {
                Response.sendResponse(exchange, 403, "Forbidden");
                return;
            }

            String[] requiredFields = new String[]{"name", "price", "quantity", "storeId"};
    
            try {
                Map<String, String> productData = Request.parseAndValidateFields(exchange.getRequestBody(), requiredFields);   // Parse and validate request body field
                Product productToUpdate = Request.productFromMap(productData); // parse the data into product object
                productToUpdate.setId(productId);
                productToUpdate.setStoreId(product.getStoreId());

                boolean isUpdated = productManager.updateProduct(productToUpdate); 
                if (isUpdated) {
                    Response.sendResponse(exchange, 200, new JSONObject(productToUpdate).toString());
                } else {
                    Response.sendResponse(exchange, 500, "Failed to update product");
                }
            } catch (IllegalArgumentException e) {
                Response.sendResponse(exchange, 400, e.getMessage());
            } catch (Exception e) {
                Response.sendResponse(exchange, 500, "Internal Server Error");
            }
        } else {
            Response.sendResponse(exchange, 400, "Invalid Request / Path");
        }
    }

}

