package org.example.entities;

import lombok.Data;

@Data
public class User {
    private String login;
    private String password;
    private int id_user;
    private String role;
    public User(){}
    public User(String login, String password, String role)
    {
        this.login = login;
        this.password = password;
        this.role = role;
    }
    public User(String login, String password)
    {
        this.login = login;
        this.password = password;
    }
    public User(int id, String login, String password, String role)
    {
        this.id_user = id;
        this.login = login;
        this.password = password;
        this.role = role;
    }

}
