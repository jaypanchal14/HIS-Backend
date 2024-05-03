package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmergencyItem {

    private String emerId;
    private String doctorId;
    private boolean handled;
    private String remark;
    private String timestamp;

}