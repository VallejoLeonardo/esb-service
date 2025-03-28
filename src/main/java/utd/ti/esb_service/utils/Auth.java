package utd.ti.esb_service.utils;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Auth {

    @Value("${jwt.secret:aJksd9QzPl+sVdK7vYc/L4dK8HgQmPpQ5K9yApUsj3w=}")
    private String secretKey;

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token.replace("Bearer ", "")) // Remove "Bearer "
                                .getBody();
            System.out.println("Token válido, usuario: " + claims.getSubject());
            return true;
        } catch (Exception e) {
            System.out.println("Error al validar el token: " + e.getMessage());
            return false;
        }
    }
}