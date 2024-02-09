package main.java.com.ldb.controller;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.java.com.ldb.model.Store;
import main.java.com.ldb.service.StoreManager;
import main.java.com.ldb.utils.Response;

public class StoreHandler implements HttpHandler {

    private final StoreManager storeManager;

    public StoreHandler() {
        this.storeManager = StoreManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        if (exchange.getRequestMethod().equals("OPTIONS")) {  
            Response.handlePreflight(exchange);  // Handle preflight request

        } else if (requestMethod.equals("GET")) {
            String path = exchange.getRequestURI().getPath(); // /api/stores/{storeId}
            String[] parts = path.split("/");
            
            if (parts.length > 3) {
                String storeIdString = parts[3]; // Assuming the URL is /api/stores/{storeId}
                int storeId = Integer.parseInt(storeIdString);
                Store store = storeManager.getStoreById(storeId);
                
                String response = getStoreDetails(store);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                Response.sendResponse(exchange, 400, "Invalid request");
            }
        } else {
            Response.sendResponse(exchange, 405, "Method not allowed for this endpoint. Please use HTTP GET method.");
        }

    }

    private String getStoreDetails(Store store) {
        // Your logic to retrieve store details goes here
        // For simplicity, we're returning a static string
        return store.toString();
    }
    
}
