package uk.co.negura.workshop_users_api.security;

public class UsernameAndPasswordAuthRequest {

    private String userName;
    private String password;

    public UsernameAndPasswordAuthRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
