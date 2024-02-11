package main.java.com.ldb.utils;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.sun.net.httpserver.HttpExchange;


public class JwtUtil {
    private static final String SECRET = "a5f1c8e6a41d5b9d73e93b4e7f050aad4d5d27373f8010ee5f5b0c8923f0df99"; // Random 256 bit key 
    // private static final String SECRET = System.getenv("JWT_SECRET"); // change to environment variable
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    // Creates a JSON Web Token using the logged in users email
    public static String createToken(int userId) {        
        Date issuedAt = new Date(); // current date
        Date expiresAt = new Date(issuedAt.getTime() + 24 * 60 * 60 * 1000); // 24 hours in milliseconds 

        String token = JWT.create()
            .withClaim("userId", userId) 
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiresAt) 
            .sign(algorithm);
        
        return token;
    }

    public static boolean verifyToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token); // if a token is verified then it's valid
            return true; 
        } catch (Exception e) {
            return false; // an exception is thrown when not being able to verify a JWT 
        }
    }

    // Extracts user ID from the token
    public static Integer extractUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("userId").asInt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Date currentDate = new Date();
            Date expireDate = decodedJWT.getExpiresAt();

            return currentDate.after(expireDate);
        } catch (Exception e) {
            return false; // assume token is expired for any exception
        }
    }

    // Extracts JWT from the Authorization header
    public static String extractJWTfromHeader(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim(); // Remove "Bearer " and trim spaces
        }
        return null;
    }

    // Method to refresh a token (renew its expiration)
    public static String refreshToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            int userId = decodedJWT.getClaim("userId").asInt();
            return createToken(userId); // Create a new token with a new expiration for the same user ID
        } catch (JWTDecodeException e) {
            return null; // Return null if the original token is invalid and cannot be refreshed
        }
    }

    public static boolean authenticate(String token) {
        // Token is considered authenticated if it's valid and not expired
        return verifyToken(token) && !isTokenExpired(token);
    }
}
