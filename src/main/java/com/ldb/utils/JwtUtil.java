package main.java.com.ldb.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {
    private static final String SECRET = "your-secret-key"; // Replace with your secret key

    public static String createToken(String email) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String token = JWT.create()
            .withClaim("email", email) // Add additional claims as needed
            .sign(algorithm);
        
        return token;
    }

    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
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
