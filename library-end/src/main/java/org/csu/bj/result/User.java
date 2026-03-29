package org.csu.bj.result;

import lombok.Data;

@Data
public class User {
    private int id;
    private String userid;
    private String password;
    private String email;
    private String phone;
    private short sex;
    private short authority;
    private short ban;
    private String headerSculpture;
}
