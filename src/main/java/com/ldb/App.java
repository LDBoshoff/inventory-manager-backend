package main.java.com.ldb;

import com.sun.net.httpserver.HttpServer;

import main.java.com.ldb.controller.ProductHandler;
import main.java.com.ldb.controller.StoreHandler;
import main.java.com.ldb.controller.UserHandler;
import main.java.com.ldb.utils.DatabaseSeeder;
import main.java.com.ldb.utils.DatabaseSetup;

import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws Exception {
        DatabaseSetup.createTables();
        DatabaseSeeder.seedData();

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

