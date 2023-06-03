package uk.co.negura.workshop_users_api.model;

public class User {

    private final Integer userID;
    private final String userName;

    public User(Integer userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public Integer getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }
}
