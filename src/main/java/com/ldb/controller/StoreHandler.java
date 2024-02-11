package main.java.com.ldb.controller;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.java.com.ldb.model.Store;
import main.java.com.ldb.service.StoreManager;
import main.java.com.ldb.utils.JwtUtil;
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
                int storeId = 0;

                try {
                    storeId = Integer.parseInt(parts[3]);
                } catch (NumberFormatException e) {
                    Response.sendResponse(exchange, 400, "Invalid store ID");
                    return;
                }

                String jwt = JwtUtil.extractJWTfromHeader(exchange);
                if (!JwtUtil.authenticate(jwt)) {
                    Response.sendResponse(exchange, 401, "Unauthorized");
                    return;
                }

                int userId = JwtUtil.extractUserIdFromToken(jwt);

                if (!storeManager.verifyOwnership(userId, storeId)) {
                    Response.sendResponse(exchange, 401, "Unauthorized - not owner");
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
        } else {
            Response.sendResponse(exchange, 405, "Method not allowed for this endpoint. Please use HTTP GET method.");
        }
    }
}