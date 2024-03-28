package org.his.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Appointment")
public class Appointment {

    @Id
    private String appointId;
    private String doctorId;
    private String patientId;
    private String admitId;
    @CreationTimestamp
    private OffsetDateTime date;

}