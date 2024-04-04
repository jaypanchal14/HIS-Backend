package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleBasedMapping {

    private Map<String, PersonalDetail> doctors;
    private Map<String, PersonalDetail> nurses;
    private Map<String, PersonalDetail> pharmacists;
    private Map<String, PersonalDetail> receptionists;

}