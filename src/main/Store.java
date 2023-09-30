package main;
public class Store {
    private int storeId;
    private int userId;
    private String storeName;

    // Constructor
    public Store(int storeId, int userId, String storeName) {
        this.storeId = storeId;
        this.userId = userId;
        this.storeName = storeName;
    }

    // Getter and Setter methods for storeId
    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    // Getter and Setter methods for userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter and Setter methods for storeName
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeId=" + storeId +
                ", userId=" + userId +
                ", storeName='" + storeName + '\'' +
                '}';
    }
}
