import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

class UserAuthenticator {
    private static final String SECRET_KEY = "jfaskjflsjfdsfdaljfksdfafdslaghjjfsafsaddsafdsagdsafjnghbslanndsafjkkjndfffdbkafnuhgflnlkfvjksfn"; // replace with your own secret key

    private static final long EXPIRATION_TIME = 30000; // token expiration time in milliseconds (1 hour)

    // Method to generate a new JWT token given a user ID
    public static String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // Method to validate a JWT token and return the user ID if valid
    public static String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}