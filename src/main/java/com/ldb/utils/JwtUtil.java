package main.java.com.ldb.utils;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {
    private static final String SECRET = "a5f1c8e6a41d5b9d73e93b4e7f050aad4d5d27373f8010ee5f5b0c8923f0df99"; // Random 256 bit key

    // Creates a JSON Web Token using the logged in users email
    public static String createToken(String email) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        
        Date issuedAt = new Date(); // current date
        Date expiresAt = new Date(issuedAt.getTime() + 24 * 60 * 60 * 1000); // 24 hours in milliseconds 

        String token = JWT.create()
            .withClaim("email", email) 
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiresAt) 
            .sign(algorithm);
        
        return token;
    }

    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWT.require(algorithm).build().verify(token); // if a token is verifie then it's valid
            return true; 
        } catch (Exception e) {
            return false; // an exception is thrown when not being able to verify a JWT 
        }
    }

    public static String extractEmailFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            
            return decodedJWT.getClaim("email").asString();
        } catch (Exception e) {
            // Handle token decoding or claim extraction errors
            throw new RuntimeException("Failed to extract email from token");
        }
    }
}
