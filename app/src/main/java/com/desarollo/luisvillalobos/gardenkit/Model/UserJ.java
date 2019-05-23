package com.desarollo.luisvillalobos.gardenkit.Model;

public class UserJ {
    protected String username,password;

    public UserJ(){

    }

    public UserJ(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
