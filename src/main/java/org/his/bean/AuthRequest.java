package org.his.bean;

import lombok.Data;
@Data
public class AuthRequest {

    private String username;
    private String role;
    private String password;

}