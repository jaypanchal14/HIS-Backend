package org.his.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Diagnosis")
public class Diagnosis {

    @Id
    private String diagnosisId;
    private String admitId;
    private String doctorId;
    private String remark;
    private String imgPath;
    @CreationTimestamp
    private OffsetDateTime date;

}
