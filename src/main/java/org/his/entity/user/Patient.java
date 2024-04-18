package org.his.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Patient")
public class Patient {

    //Patient-id would be his/her AADHAAR card
    @Id
    private String aadhar;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String gender;

    private Date birthDate;

    @Size(max = 255)
    private String phoneNumber;

    @Size(max = 255)
    private String bloodGroup;

    @Size(max = 512)
    private String address;

    @Size(max = 255)
    private String profileImage;

    @Size(max = 255)
    private String patientType;

    @Size(max = 255)
    private String wardNo;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

}