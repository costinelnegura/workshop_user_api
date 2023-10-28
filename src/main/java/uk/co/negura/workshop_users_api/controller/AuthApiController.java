package uk.co.negura.workshop_users_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.negura.workshop_users_api.api.AuthRequest;
import uk.co.negura.workshop_users_api.service.AuthApiService;

import javax.validation.Valid;

/*
this method handles user login requests, checks if the provided username and password are correct, and if they are,
it provides the user with a secure token (JWT) that can be used for subsequent authenticated requests.
If the credentials are incorrect, it returns an error response.
 */
@RestController
public class AuthApiController {

    @Autowired
    private AuthApiService authApiService;

    @PostMapping(value = "/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
        return authApiService.userLogin(authRequest);
    }
}
