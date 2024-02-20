package main.java.com.ldb.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import main.java.com.ldb.model.OrderItem;
import main.java.com.ldb.service.OrderManager;
import main.java.com.ldb.service.ProductManager;
import main.java.com.ldb.service.StoreManager;
import main.java.com.ldb.utils.JwtUtil;
import main.java.com.ldb.utils.Request;
import main.java.com.ldb.utils.Response;

public class OrderHandler implements HttpHandler {

    private final ProductManager productManager;
    private final StoreManager storeManager;
    private final OrderManager orderManager;

    public OrderHandler() {
        this.productManager = ProductManager.getInstance();
        this.storeManager = StoreManager.getInstance();
        this.orderManager = OrderManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        if ("OPTIONS".equals(requestMethod)) {  
            Response.handlePreflight(exchange);  // Handle preflight request

        } else if ("POST".equals(requestMethod)) {
            try {
                String[] requiredOrderFields = new String[]{"storeId", "customerName", "customerEmail", "customerPhoneNumber", "customerAddress", "items"};
                Map<String, String> orderData = Request.parseAndValidateFields(exchange.getRequestBody(), requiredOrderFields);// Parse and validate order fields

                for (String field : requiredOrderFields) {
                    String value = orderData.get(field);
                    System.out.println(field + ": " + value); // This will print to your terminal
                }

                String items = orderData.get("items");
                System.out.println("items String = " + items);

                String itemsJsonString = orderData.get("items");
                // Assuming you have a method parseItemsStringToList that converts the JSON string to List<OrderItem>
                List<OrderItem> orderItems = Request.parseItemsStringToList(itemsJsonString);

                // For demonstration, printing items to terminal
                for (OrderItem item : orderItems) {
                    System.out.println(item.toString());
                }

                

                Response.sendResponse(exchange, 200, "Order placed successfully.");
            } catch (IllegalArgumentException e) {
                Response.sendResponse(exchange, 400, "Bad Request: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace(); // For debugging
                Response.sendResponse(exchange, 500, "Internal Server Error");
            }
        } else {
            Response.sendResponse(exchange, 405, "Method not allowed for this endpoint.");
        }
    }

    
}




// FRONT END ORDER JSON
// {
//     "storeId": "1",
//     "customerName": "John Doe",
//     "customerEmail": "john.doe@example.com",
//     "customerPhoneNumber": "1234567890",
//     "customerAddress": "123 Main Street, Anytown, USA",
//     "items": [
//       {
//         "productId": 101,
//         "price": 15.99,
//         "quantity": 2
//       },
//       {
//         "productId": 102,
//         "price": 25.99,
//         "quantity": 1
//       }
//     ]
//   }
  