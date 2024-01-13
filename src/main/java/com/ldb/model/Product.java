package main.java.com.ldb.model;

public class Product {
    private int id;
    private int storeId;
    private String productName;
    private String sku;
    private String description;
    private int quantity;

    // Constructor
    public Product(int id, int storeId, String productName, String sku, String description, int quantity) {
        this.id = id;
        this.storeId = storeId;
        this.productName = productName;
        this.sku = sku;
        this.description = description;
        this.quantity = quantity;
    }

    // Getter and Setter methods for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter methods for storeId
    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    // Getter and Setter methods for productName
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Getter and Setter methods for sku
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    // Getter and Setter methods for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and Setter methods for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", storeId=" + storeId +
                ", productName='" + productName + '\'' +
                ", sku='" + sku + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
