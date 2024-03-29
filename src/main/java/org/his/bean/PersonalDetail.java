package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalDetail {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private Date birthDate;
    private String blood;
    private String specialization;
    private String department;
    private int experience;
    private String address;
    private String profileImage;

    //Only for head-nurse
    private boolean isHead;

}