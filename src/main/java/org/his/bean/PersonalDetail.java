package org.his.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalDetail {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private String birthDate;
    private String blood;
    private String specialization;
    private String department;
    private int experience;
    private String address;

    //For doctor and nurse
    private int mon;
    private int tue;
    private int wed;
    private int thu;
    private int fri;
    private int sat;
    private int sun;

    //This would be in Base64 encoded string
    private String profileImage;

    //Only for head-nurse
    private boolean isHead;
    //Used by admin for checking
    private String role;
    private String userId;
    private boolean isActive;

}