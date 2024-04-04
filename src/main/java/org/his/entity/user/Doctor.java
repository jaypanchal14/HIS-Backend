package org.his.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Doctor")
public class Doctor {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;
    private String phoneNumber;
    private String bloodGroup;
    private String specialization;
    private String department;
    //store it number of months
    private int experience;
    private String address;

    //To store the path where image is actually stored
    private String profileImage;

    // value of below seven fields will be between 0-3
    private int mon;
    private int tue;
    private int wed;
    private int thu;
    private int fri;
    private int sat;
    private int sun;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

}