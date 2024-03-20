package uk.co.negura.workshop_users_api.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.co.negura.workshop_users_api.model.UserEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/*
this class and its methods provide functionality for creating JWT tokens based on user information,
validating the authenticity of tokens, and extracting user-related data from tokens.
JWTs are commonly used for authentication and authorization in web applications to securely transmit user identity
and access privileges.
 */
@Component
public class JwtTokenUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final Long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000L; // 24hours

    @Value("${app.jwt.secretKey}")
    private String secretKey;

    /*
    This method generates a JWT token based on the provided user information.
     */
    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .issuer("workshop_ltd")
                .subject(user.getId().toString())
                .audience().add("workshop_user").and()
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString()) // just an example id
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .claim("authorities", user.getAuthorities())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    /*
    This method extracts user details from a JWT token and returns them as a HashMap.
     */

    public HashMap<String, String> extractUserDetails(String token) {
        var extractedUserDetails = new HashMap<String, String>();
        extractedUserDetails.put("userId", parseClaims(token).get("userId").toString());
        extractedUserDetails.put("email", (String) parseClaims(token).get("email"));
        extractedUserDetails.put("username", (String) parseClaims(token).get("username"));
        return extractedUserDetails;
    }

    /*
    This method parses the claims (payload) of a JWT token.
     */
    public Claims parseClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
 This method validates the provided JWT token. It checks if the token is correctly formed, has a valid signature, and is not expired.
 If the token is valid, it returns a ResponseEntity with HTTP status OK.
 If the token is expired, it logs the error and returns a ResponseEntity with HTTP status UNAUTHORIZED and body "Expired JWT token".
 If the token is malformed, it logs the error and returns a ResponseEntity with HTTP status UNAUTHORIZED and body "Malformed JWT token".
 If the token has an invalid signature, it logs the error and returns a ResponseEntity with HTTP status UNAUTHORIZED and body "Invalid JWT signature".
 If the token is unsupported, it logs the error and returns a ResponseEntity with HTTP status UNAUTHORIZED and body "Unsupported JWT token".
 If the token is invalid for any other reason, it logs the error and returns a ResponseEntity with HTTP status UNAUTHORIZED and body "Invalid JWT token".
*/
    public ResponseEntity<?> validateToken(String token) {
        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(getToken(token)); // Deprecated method
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseSignedClaims(getToken(token));
            return ResponseEntity.ok().build();
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired JWT token");
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        } catch (MalformedJwtException e) {
            LOGGER.error("Malformed JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Malformed JWT token");
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT signature");
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unsupported JWT token");
        }
    }

    public String getToken (String bearerToken) {
        return bearerToken.substring(7);
    }

}
