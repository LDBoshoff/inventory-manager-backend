package main.java.com.ldb.controller;

import java.io.IOException;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.java.com.ldb.model.Store;
import main.java.com.ldb.service.StoreManager;
import main.java.com.ldb.utils.JwtUtil;
import main.java.com.ldb.utils.Request;
import main.java.com.ldb.utils.Response;

public class StoreHandler implements HttpHandler {

    private final StoreManager storeManager;

    public StoreHandler() {
        this.storeManager = StoreManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!JwtUtil.isAuthenticated(exchange)) {
            Response.sendResponse(exchange, 401, "Unauthorized");
            return;
        }

        String requestMethod = exchange.getRequestMethod();

        if ("OPTIONS".equals(requestMethod)) {  
            Response.handlePreflight(exchange);  // Handle preflight request

        } else if ("GET".equals(requestMethod)) {
            String path = exchange.getRequestURI().getPath(); // /api/stores/{storeId}
            String[] parts = path.split("/");
            
            if (parts.length > 3) {
                int storeId = validateStoreId(parts[3]);
                if (storeId == -1) {
                    Response.sendResponse(exchange, 400, "Invalid store ID");
                }

                if (!isAuthorized(exchange, storeId)) {
                    Response.sendResponse(exchange, 401, "Unauthorized");
                    return;
                }

                Store store = storeManager.getStoreById(storeId);
                if (store == null) {
                    Response.sendResponse(exchange, 404, "Store not found");
                    return;
                }

                Response.sendResponse(exchange, 200, store.toString()); 
            } else {
                Response.sendResponse(exchange, 400, "Invalid request");
            }
        
        } else if ("PUT".equals(requestMethod)) {
            // Handle store update
            String path = exchange.getRequestURI().getPath(); // /api/stores/{storeId}
            String[] parts = path.split("/");
            
            if (parts.length > 3) {
                try {
                    int storeId = validateStoreId(parts[3]);
                    if (storeId == -1) {
                        Response.sendResponse(exchange, 400, "Invalid store ID");
                    }

                    if (!isAuthorized(exchange, storeId)) {
                        Response.sendResponse(exchange, 401, "Unauthorized");
                        return;
                    }


                    Map<String, String> fieldValues = Request.parseAndValidateFields(exchange.getRequestBody(), new String[]{"name"});
                    int userId = JwtUtil.extractUserIdFromToken(JwtUtil.extractJWTfromHeader(exchange));
                    Store updatedStore = new Store(storeId, userId, fieldValues.get("name"));
                    // Update the store details
                    boolean updateSuccess = storeManager.updateStore(updatedStore);
                    if (!updateSuccess) {
                        Response.sendResponse(exchange, 500, "Failed to update store");
                        return;
                    }
                
                    Response.sendResponse(exchange, 200, "Store updated successfully");
                } catch (IllegalArgumentException e) {
                    Response.sendResponse(exchange, 400, e.getMessage());
                }
            } else {
                Response.sendResponse(exchange, 400, "Invalid request");
            }
        
        } else {
            Response.sendResponse(exchange, 405, "Method not allowed for this endpoint.");
        }
    }

    private int validateStoreId(String part) throws IOException {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

   private boolean isAuthorized(HttpExchange exchange, int storeId) { 
        String token = JwtUtil.extractJWTfromHeader(exchange);
        int userId = JwtUtil.extractUserIdFromToken(token);
        
        return storeManager.verifyOwnership(userId, storeId);
    }

}

