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
@Table(name = "Nurse")
public class Nurse {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;
    private String phoneNumber;
    private String bloodGroup;
    private String specialization;
    //store it as number of month
    private int experience;
    private String address;
    private String profileImage;
    private boolean isHead;

    // value of below seven field will be between 0-3
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