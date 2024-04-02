package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientDetail {

    private String receptionId;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private String patientType;
    private String birthDate;
    private String blood;
    private String address;
    private String imagePath;
    private String wardNo;
    private String action;

    private String admitId;


}