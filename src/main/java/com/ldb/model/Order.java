package main.java.com.ldb.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id; // Optional: Use Integer to allow for null if not set, representing a new order
    private int storeId;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerAddress;
    private List<OrderItem> items;
    private double totalAmount;

    public Order(int storeId, String customerName, String customerEmail, String customerPhoneNumber, String customerAddress) {
        this.storeId = storeId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerAddress = customerAddress;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public Order(Integer id, int storeId, String customerName, String customerEmail, String customerPhoneNumber, String customerAddress) {
        this(storeId, customerName, customerEmail, customerPhoneNumber, customerAddress); // Call the other constructor to initialize common fields
        this.id = id;
    }

    // Method to add an item to the order and update the total amount
    public void addItem(OrderItem item) {
        items.add(item);
        totalAmount += item.getPrice() * item.getQuantity();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", storeId=" + storeId +
               ", customerName='" + customerName + '\'' +
               ", customerEmail='" + customerEmail + '\'' +
               ", customerPhoneNumber='" + customerPhoneNumber + '\'' +
               ", customerAddress='" + customerAddress + '\'' +
               ", items=" + items +
               ", totalAmount=" + totalAmount +
               '}';
    }
}
