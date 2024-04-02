package org.his.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Prescription")
public class Prescription {

    @Id
    private String presId;
    private String patientId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String medicine;
    private String diagnosisId;
    private String pharmaId;
    private String doctorId;
    @CreationTimestamp
    private OffsetDateTime date;

}