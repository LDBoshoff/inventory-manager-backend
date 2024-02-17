package main.java.com.ldb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import main.java.com.ldb.model.Product;

public class Request {
    // Helper method to check if a string is null or empty
    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Helper method to check if the request body is empty
    private static boolean emptyRequest(InputStream inputStream) {
        return inputStream == null;
    }

    // Helper method to parse the JSON request
    private static JSONObject parseJsonRequest(InputStream inputStream) throws IOException {
        try {
            return new JSONObject(new JSONTokener(inputStream));
        } catch (JSONException e) {
            return null;
        }
    }

    public static Map<String, String> parseAndValidateFields(InputStream requestBody, String[] requiredFields) throws IOException {
        if (Request.emptyRequest(requestBody)) {
            throw new IllegalArgumentException("Bad Request - Empty Request");
        }

        JSONObject jsonObject = parseJsonRequest(requestBody);
        if (jsonObject == null) {
            throw new IllegalArgumentException("JSON EXCEPTION");
        }

        Map<String, String> fieldValues = new HashMap<>();
        for (String field : requiredFields) {
            String value = jsonObject.optString(field);
            if (Request.isNullOrEmpty(value)) {
                throw new IllegalArgumentException("Bad Request - Missing or empty required data in JSON request");
            }
            fieldValues.put(field, value);
        }
        return fieldValues;
    }

    public static Product productFromMap(Map<String, String> productData) throws IllegalArgumentException {
        try {
            String name = productData.get("name");
            double price = Double.parseDouble(productData.get("price"));
            int quantity = Integer.parseInt(productData.get("quantity"));
            int storeId = Integer.parseInt(productData.get("storeId"));
            return new Product(name, price, quantity, storeId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid product data value provided.");
        }
    }

    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryMap = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                try {
                    // URL decoding is omitted for simplicity but should be considered in real-world applications
                    String key = idx > 0 ? pair.substring(0, idx) : pair;
                    String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
                    queryMap.put(key, value);
                } catch (Exception e) {
                    // Handle exceptions or invalid parameters as necessary
                    System.err.println("Error parsing query string: " + e.getMessage());
                }
            }
        }
        return queryMap;
    }
}
