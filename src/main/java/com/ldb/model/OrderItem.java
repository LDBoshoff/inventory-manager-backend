package main.java.com.ldb.model;

public class OrderItem {
    private Integer id; // Optional: Use Integer to allow for null if not set, representing a new order item
    private String productName; // Replacing productId with a Product object
    private double price;
    private int quantity;

    // Constructor for creating a new OrderItem instance without an ID, using a Product object
    public OrderItem(String productName, double price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Constructor for creating an OrderItem instance with an ID, using a Product object
    public OrderItem(Integer id, String productName, double price, int quantity) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", product=" + getProductName() +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
