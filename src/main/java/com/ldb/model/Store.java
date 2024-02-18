package main.java.com.ldb.model;

public class Store {
    private Integer id; // Use Integer to allow for null if not set
    private int userId;
    private String name;
    private double salesRevenue; // Added field for sales revenue
    private int totalSales; // Added field for total number of sales

    // Constructor for a newly created store (without an ID, salesRevenue, or totalSales)
    public Store(int userId, String name) {
        this.userId = userId;
        this.name = name;
        // Initialize salesRevenue and totalSales to 0 for a new store
        this.salesRevenue = 0.0;
        this.totalSales = 0;
    }

    // Constructor for a store retrieved from the database (with an ID, including salesRevenue and totalSales)
    public Store(Integer id, int userId, String name, double salesRevenue, int totalSales) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.salesRevenue = salesRevenue;
        this.totalSales = totalSales;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalesRevenue() {
        return salesRevenue;
    }

    public void setSalesRevenue(double salesRevenue) {
        this.salesRevenue = salesRevenue;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    // Existing getters and setters...

    @Override
    public String toString() {
        return "Store{" +
               "id=" + id +
               ", userId=" + userId +
               ", name='" + name + '\'' +
               ", salesRevenue=" + salesRevenue +
               ", totalSales=" + totalSales +
               '}';
    }
}

