package main.java.com.ldb;

import com.sun.net.httpserver.HttpServer;

import main.java.com.ldb.controller.ProductHandler;
import main.java.com.ldb.controller.StoreHandler;
import main.java.com.ldb.controller.UserHandler;
import main.java.com.ldb.model.Product;
import main.java.com.ldb.model.Store;
import main.java.com.ldb.model.User;
import main.java.com.ldb.service.ProductManager;
import main.java.com.ldb.service.StoreManager;
import main.java.com.ldb.service.UserManager;

import java.net.InetSocketAddress;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {

        int port = 8080;                                                       // Define the port for the server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); // Create an HTTP server
        
        server.createContext("/api/users", new UserHandler()); // API endpoint for account register and login
        server.createContext("/api/stores", new StoreHandler());   // API endpoint for GET all stores belonging to specific user
        server.createContext("/api/products", new ProductHandler());

        server.setExecutor(null); // Use the default executor
        server.start();                    // Start the server
        System.out.println("Server started on port " + port);  

    }

}

