package uk.co.negura.workshop_users_api.api;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;

public class AuthRequest {

    @Email
    @Length(min = 6, max = 128)
    private String email;

    @Length(min = 6, max = 128)
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
