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

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    private String phone;
    private String gender;
    private String birthDate;
    private String blood;
    private String specialization;
    private String department;
    private int experience;
    private String address;
    private String profileImage;

    //Only for head-nurse
    private boolean isHead;
    //Used by admin for checking
    private String role;
    private String userId;
    private boolean isActive;

}