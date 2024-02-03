package main.java.com.ldb.model;

public class Store {
    private Integer id; // Use Integer to allow for null if not set
    private int userId;
    private String name;

    // Constructor for a newly created store (without an ID)
    public Store(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // Constructor for a store retrieved from the database (with an ID)
    public Store(Integer id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
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
}

