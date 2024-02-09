package main.java.com.ldb.model;

public class Product {
    // Private fields
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int storeId;

    // Constructor for new product creation (without ID)
    public Product(String name, double price, int quantity, int storeId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    // Constructor for existing product retrieval (with ID)
    public Product(int id, String name, double price, int quantity, int storeId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStoreId() {
        return storeId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    // Optionally, override the toString() method
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", storeId=" + storeId +
                '}';
    }
}
