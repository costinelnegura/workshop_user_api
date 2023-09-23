package uk.co.negura.workshop_users_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.security.JWTUtil;
import uk.co.negura.workshop_users_api.security.UsernameAndPasswordAuthRequest;
import uk.co.negura.workshop_users_api.service.WorkshopUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final WorkshopUserService workshopUserService;
    private final JWTUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          WorkshopUserService workshopUserService,
                          JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.workshopUserService = workshopUserService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody UsernameAndPasswordAuthRequest usernameAndPasswordAuthRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameAndPasswordAuthRequest.getUserName(),
                        usernameAndPasswordAuthRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = workshopUserService.loadUserByUsername(usernameAndPasswordAuthRequest.getUserName());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Implement user registration logic here, including password hashing
        // You can use userDetailsService to save the user to the database
        // Return an appropriate response
        return ResponseEntity.ok("User registered successfully!");
    }
}
