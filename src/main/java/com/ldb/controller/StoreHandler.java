package main.java.com.ldb.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

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
        String requestMethod = exchange.getRequestMethod();

        if ("OPTIONS".equals(requestMethod)) {  
            Response.handlePreflight(exchange);  // Handle preflight request

        } else if ("GET".equals(requestMethod)) {
            String path = exchange.getRequestURI().getPath(); // /api/stores/{storeId}
            String[] parts = path.split("/");
            
            if (parts.length > 3) {
                int storeId = validateStoreId(parts[3], exchange);
                if (storeId == -1) {
                    Response.sendResponse(exchange, 400, "Invalid store ID");
                }

                if (authenticateAndAuthorize(exchange, storeId)) {
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
                    int storeId = validateStoreId(parts[3], exchange);
                    if (storeId == -1) {
                        Response.sendResponse(exchange, 400, "Invalid store ID");
                    }

                    if (authenticateAndAuthorize(exchange, storeId)) {
                        Response.sendResponse(exchange, 401, "Unauthorized");
                        return;
                    }

                    Map<String, String> fieldValues = parseAndValidateFields(exchange.getRequestBody(), new String[]{"name"});
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

    private int validateStoreId(String part, HttpExchange exchange) throws IOException {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            Response.sendResponse(exchange, 400, "Invalid store ID");
            return -1;
        }
    }

    private boolean authenticateAndAuthorize(HttpExchange exchange, int storeId) throws IOException {
        String jwt = JwtUtil.extractJWTfromHeader(exchange);
        int userId = JwtUtil.extractUserIdFromToken(jwt);

        if (!JwtUtil.authenticate(jwt) && !storeManager.verifyOwnership(userId, storeId)) {
            return false;
        }
        
        return true;
    }

    private Map<String, String> parseAndValidateFields(InputStream requestBody, String[] requiredFields) throws IOException {
        if (Request.emptyRequest(requestBody)) {
            throw new IllegalArgumentException("Bad Request - Empty Request");
        }

        JSONObject jsonObject = Request.parseJsonRequest(requestBody);
        if (jsonObject == null) {
            throw new IllegalArgumentException("JSON EXCEPTION");
        }

        Map<String, String> fieldValues = new HashMap<>();
        for (String field : requiredFields) {
            String value = jsonObject.optString(field);
            if (Request.isNullOrEmpty(value)) {
                throw new IllegalArgumentException("Bad Request - Missing or empty required data in JSON request");
            }
            fieldValues.put(field, value);
        }
        return fieldValues;
    }
}

