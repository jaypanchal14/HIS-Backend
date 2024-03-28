package org.his.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Patient")
public class Patient {

    //Patient-id would be his/her aadhar card
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private Date birthDate;
    private String phoneNumber;
    private String bloodGroup;
    private String address;
    private String profileImage;
    private String patientType;
    private String wardNo;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

}