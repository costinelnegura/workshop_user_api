package uk.co.negura.workshop_users_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.negura.workshop_users_api.api.AuthRequest;
import uk.co.negura.workshop_users_api.api.AuthResponse;
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.util.JwtTokenUtil;

import javax.validation.Valid;

/*
this method handles user login requests, checks if the provided username and password are correct, and if they are,
it provides the user with a secure token (JWT) that can be used for subsequent authenticated requests.
If the credentials are incorrect, it returns an error response.
 */
@RestController
public class AuthApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
