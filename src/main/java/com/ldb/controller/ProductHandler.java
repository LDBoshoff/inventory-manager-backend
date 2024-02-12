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
import main.java.com.ldb.utils.JwtUtil;
import main.java.com.ldb.utils.Request;
import main.java.com.ldb.utils.Response;

public class ProductHandler implements HttpHandler {

    private final ProductManager productManager;

    public ProductHandler() {
        this.productManager = ProductManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        if ("OPTIONS".equals(requestMethod)) {
            Response.handlePreflight(exchange);
        } else if ("GET".equals(requestMethod)) { 
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            
            Map<String, String> queryParams = parseQueryString(query);

            if (path.equals("/api/products") && queryParams.containsKey("storeId")) {
                System.out.println("path = " + path + ", query = " + query);
               
                // // Retrieve all products for a specific store
                int storeId = validateStoreId(queryParams.get("storeId"));
                if (storeId == -1) {
                    Response.sendResponse(exchange, 400, "Invalid store ID");
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
                System.out.println("product id = " + productId);
                Product product = productManager.getProductById(productId);
                
                if (product == null) {
                    Response.sendResponse(exchange, 404, "Product not found");
                } else {
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
     
    // private int validateProductId(String part, HttpExchange exchange) throws IOException {
    //     try {
    //         return Integer.parseInt(part);
    //     } catch (NumberFormatException e) {
    //         Response.sendResponse(exchange, 400, "Invalid product ID");
    //         return -1;
    //     }
    // }

    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryMap = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                try {
                    // URL decoding is omitted for simplicity but should be considered in real-world applications
                    String key = idx > 0 ? pair.substring(0, idx) : pair;
                    String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
                    queryMap.put(key, value);
                } catch (Exception e) {
                    // Handle exceptions or invalid parameters as necessary
                    System.err.println("Error parsing query string: " + e.getMessage());
                }
            }
        }
        return queryMap;
    }
}

