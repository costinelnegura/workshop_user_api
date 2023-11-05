package uk.co.negura.workshop_users_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import uk.co.negura.workshop_users_api.api.AuthRequest;
import uk.co.negura.workshop_users_api.api.AuthResponse;
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.util.JwtTokenUtil;

@Service
public class AuthApiService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /*
    Authenticate the user using the email and password.
    Generate a JWT token for the user.

     */
    public ResponseEntity<?> userLogin(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            String token = jwtTokenUtil.generateToken(user);
            AuthResponse authResponse = new AuthResponse(user.getEmail(), token);
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(
                    "Authenticate", "Invalid credentials"
            ).build();

        }
    }
}
