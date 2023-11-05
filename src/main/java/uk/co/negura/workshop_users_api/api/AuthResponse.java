package uk.co.negura.workshop_users_api.api;

public class AuthResponse {

    private String email;

    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
