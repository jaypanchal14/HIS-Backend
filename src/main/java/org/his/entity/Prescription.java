package org.his.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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
    private String medicine;
    private String diagnosisId;
    private String pharmaId;
    private String doctorId;
    @CreationTimestamp
    private OffsetDateTime date;

}