package main.java.com.ldb.controller;

import java.io.IOException;
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
        String requestMethod = exchange.getRequestMethod();

        if ("OPTIONS".equals(requestMethod)) {
            Response.handlePreflight(exchange);
        } else if ("GET".equals(requestMethod)) { 
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            
            Map<String, String> queryParams = Request.parseQueryString(query);

            if (path.equals("/api/products") && queryParams.containsKey("storeId")) {
                // // Retrieve all products for a specific store
                int storeId = validateStoreId(queryParams.get("storeId"));
                if (storeId == -1) {
                    Response.sendResponse(exchange, 400, "Invalid store ID");
                    return;
                }

                if (!authenticateAndAuthorize(exchange, storeId)) {
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
            } else if (path.matches("/api/products/\\d+")) { // Regex to match /api/products/{productId}
                // Retrieve a specific product by productId
                int productId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

                Product product = productManager.getProductById(productId);
                
                if (product == null) {
                    Response.sendResponse(exchange, 404, "Product not found");
                } else {

                    if (!authenticateAndAuthorize(exchange, product.getStoreId())) {
                        Response.sendResponse(exchange, 401, "Unauthorized");
                        return;
                    }

                    Response.sendResponse(exchange, 200, new JSONObject(product).toString());
                }
            } else {
                Response.sendResponse(exchange, 400, "Invalid request");
            }
        }
    }

    private int validateStoreId(String storeIdString) throws IOException {
        try {
            return Integer.parseInt(storeIdString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean authenticateAndAuthorize(HttpExchange exchange, int storeId) throws IOException {
        String jwt = JwtUtil.extractJWTfromHeader(exchange);
        if (jwt == null) {
            return false;
        }

        int userId = JwtUtil.extractUserIdFromToken(jwt);

        if (!JwtUtil.authenticate(jwt) || !storeManager.verifyOwnership(userId, storeId)) {
            return false;
        }
        
        return true;
    }

     
    // private int validateProductId(String part, HttpExchange exchange) throws IOException {
    //     try {
    //         return Integer.parseInt(part);
    //     } catch (NumberFormatException e) {
    //         Response.sendResponse(exchange, 400, "Invalid product ID");
    //         return -1;
    //     }
    // }
}

