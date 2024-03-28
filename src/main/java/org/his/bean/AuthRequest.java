package org.his.bean;

import lombok.Data;
@Data
public class AuthRequest {

    private String role;
    //Below two used for authentication
    private String username;
    private String password;

    //Below three used for passwordUpdate
    private String userId;
    private String oldPassword;
    private String newPassword;

}