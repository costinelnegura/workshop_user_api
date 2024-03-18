package uk.co.negura.workshop_users_api;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.negura.workshop_users_api.model.UserEntity;
import uk.co.negura.workshop_users_api.util.JwtTokenUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestJwtTokenUtil {
//    private JwtTokenUtil jwtTokenUtil;
//    private UserEntity userEntity;
//
//    @BeforeEach
//    void setUp() {
//        jwtTokenUtil = new JwtTokenUtil();
//        userEntity = mock(UserEntity.class);
//    }
//
//    @Test
//    void generateTokenReturnsValidToken() {
//        String token = jwtTokenUtil.generateToken(userEntity);
//        assertNotNull(token);
//    }
//
//    @Test
//    void validateTokenReturnsOkForValidToken() {
//        String token = jwtTokenUtil.generateToken(userEntity);
//        ResponseEntity<?> response = jwtTokenUtil.validateToken(token);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//
//    @Test
//    void validateTokenReturnsUnauthorizedForExpiredToken() {
//        String expiredToken = "expiredToken";
//        when(jwtTokenUtil.validateToken(expiredToken)).thenThrow(ExpiredJwtException.class);
//        ResponseEntity<?> response = jwtTokenUtil.validateToken(expiredToken);
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    void validateTokenReturnsUnauthorizedForMalformedToken() {
//        String malformedToken = "malformedToken";
//        when(jwtTokenUtil.validateToken(malformedToken)).thenThrow(MalformedJwtException.class);
//        ResponseEntity<?> response = jwtTokenUtil.validateToken(malformedToken);
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    void validateTokenReturnsUnauthorizedForInvalidSignature() {
//        String tokenWithInvalidSignature = "tokenWithInvalidSignature";
//        when(jwtTokenUtil.validateToken(tokenWithInvalidSignature)).thenThrow(SignatureException.class);
//        ResponseEntity<?> response = jwtTokenUtil.validateToken(tokenWithInvalidSignature);
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    void validateTokenReturnsUnauthorizedForUnsupportedToken() {
//        String unsupportedToken = "unsupportedToken";
//        when(jwtTokenUtil.validateToken(unsupportedToken)).thenThrow(UnsupportedJwtException.class);
//        ResponseEntity<?> response = jwtTokenUtil.validateToken(unsupportedToken);
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
}
