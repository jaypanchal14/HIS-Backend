package org.his.bean;

import lombok.Data;

@Data
public class UpdateAccStatusReq {

    private String role;
    private String userId;
    private String adminId;
    private String action;

}