package org.his.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewUserRequest{

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    @NotBlank
    private String birthDate;
    private String blood;
    private String specialization;
    private String department;
    private int experience;
    private String address;
    private String profileImage;

    //Only for head-nurse
    private int isHead;
    //@NotBlank
    private String role;

}