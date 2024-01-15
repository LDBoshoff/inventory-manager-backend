package main.java.com.ldb.utils;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Request {
    // Helper method to check if a string is null or empty
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Helper method to check if the request body is empty
    public static boolean emptyRequest(InputStream inputStream) {
        return inputStream == null;
    }

    // Helper method to parse the JSON request
    public static JSONObject parseJsonRequest(InputStream inputStream) throws IOException {
        try {
            return new JSONObject(new JSONTokener(inputStream));
        } catch (JSONException e) {
            return null;
        }
    }
}
