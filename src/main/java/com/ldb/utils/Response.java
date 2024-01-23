package main.java.com.ldb.utils;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class Response {
    
    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static void handlePreflight(HttpExchange exchange) throws IOException {
        // Set appropriate headers for preflight response
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*"); // Adjust the origin based on your needs
        headers.add("Access-Control-Allow-Methods", "POST, OPTIONS"); // Include other allowed methods
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization"); // Include other allowed headers
        headers.add("Access-Control-Max-Age", "86400"); // 24 hours cache
    
        // Respond with a successful status code for OPTIONS requests
        exchange.sendResponseHeaders(200, -1);
    
        // Close the exchange
        exchange.close();
    }
}
