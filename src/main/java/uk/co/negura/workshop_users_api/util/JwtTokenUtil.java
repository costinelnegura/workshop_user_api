package uk.co.negura.workshop_users_api.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.co.negura.workshop_users_api.model.UserEntity;

import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;

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
    his method generates a JWT token based on user information.
    It takes a User object as input, typically containing user details.
    It constructs a JWT token using the "Jwts.builder()" method, setting various claims such as subject (user ID and email),
    issuer, issued date, expiration date, and signing it with a secret key.
    It returns the JWT token as a string.
     */
    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getId() + "," + user.getEmail() + "," + user.getUsername())
                .setIssuer("workshop_ltd")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(HS512, secretKey)
                .compact();
    }

    /*
    This method extracts and returns the subject claim from a JWT token.
    The subject claim often contains user-specific information, in this case, the user's ID and email.
     */
    public String getSubject(String token) {
        return parseClaims(token)
                .getSubject();
    }

    /*
    This method parses the claims (payload) of a JWT token.
    It takes a JWT token as input.
    It parses and returns the claims as a Claims object, which can be used to access the data stored within the token's payload.
     */
    public Claims parseClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
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
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(getToken(token));
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
