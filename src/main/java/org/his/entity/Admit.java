package org.his.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Admit")
public class Admit {

    @Id
    private String admitId;
    //To check is OP or IP
    private String patientType;
    private boolean isActive;
    private boolean isEmergency;
    private String patientId;
    private String remark;
    private OffsetDateTime date;

}