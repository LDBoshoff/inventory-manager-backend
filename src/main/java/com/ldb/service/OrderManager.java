package main.java.com.ldb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import main.java.com.ldb.model.Order;
import main.java.com.ldb.model.OrderItem;
import main.java.com.ldb.utils.DatabaseConnection;

public class OrderManager {
    private static OrderManager instance;
    private Connection connection;

    private OrderManager() {
        connection = DatabaseConnection.getConnection();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    

    public boolean placeOrder(Order order) {
        try {
            // 1. Decrement Product Quantities
            String updateProductSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            PreparedStatement updateProductStmt = connection.prepareStatement(updateProductSql);
            for (OrderItem item : order.getItems()) {
                updateProductStmt.setInt(1, item.getQuantity());
                updateProductStmt.setInt(2, item.getProductId());
                updateProductStmt.executeUpdate();
            }

            // 2. Update Store Revenue and Total Sales
            String updateStoreSql = "UPDATE stores SET sales_revenue = sales_revenue + ?, total_sales = total_sales + 1 WHERE id = ?";
            PreparedStatement updateStoreStmt = connection.prepareStatement(updateStoreSql);
            updateStoreStmt.setDouble(1, order.getTotalAmount());
            updateStoreStmt.setInt(2, order.getStoreId());
            updateStoreStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
