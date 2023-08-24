package backendspring.com.backendspring.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

  @Value("${jwt.secret}") // Inyecta el secreto desde la configuración (application.properties)
  private String secret;

  @Value("${jwt.expiration}") // Inyecta la duración de expiración desde la configuración
  private Long expiration;

  private final Key secretKey;

  public JwtTokenUtil() {
    this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  }

  // Generar un token JWT
  public String generateToken(String subject) {
    Map<String, Object> claims = new HashMap<>();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  // Validar si un token está expirado
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // Extraer la fecha de expiración de un token
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Extraer información del token (claims)
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    System.out.println("TOKEN 1 " + token);
    System.out.println("TOKEN 2 " + Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token));
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
  }
}
