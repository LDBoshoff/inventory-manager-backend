package main.java.com.ldb.model;

public class OrderItem {
    private Integer id; // Optional: Use Integer to allow for null if not set, representing a new order item
    private int productId; // Now using productId instead of productName
    private double price;
    private int quantity;

    // Constructor for creating a new OrderItem instance without an ID, using a Product ID
    public OrderItem(int productId, double price, int quantity) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    // Constructor for creating an OrderItem instance with an ID, using a Product ID
    public OrderItem(Integer id, int productId, double price, int quantity) {
        this.id = id;
        this.productId = productId;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
                ", productId=" + productId +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
