package org.his.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RoleCountResp {

    private Map<String, Long> response;
    //private List<RoleCount> response;
    private String error;

}