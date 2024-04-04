package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewUserResponse {

    private List<PersonalDetail> doctor;
    private List<PersonalDetail> nurse;
    private List<PersonalDetail> pharmacist;
    private List<PersonalDetail> receptionist;
    private String error;

}