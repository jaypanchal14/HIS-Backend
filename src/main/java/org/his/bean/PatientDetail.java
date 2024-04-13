package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientDetail {

    private String receptionId;
    private int isNewPatient;
    private String aadhaar;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private String patientType;   //In registering, it would be by-default OP
    private String birthDate;
    private String blood;
    private String address;
    private MultipartFile image;
    private String remark;

    private String wardNo;
    private String action;
    private String admitId;


}