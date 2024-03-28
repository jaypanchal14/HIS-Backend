package org.his.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "WardHistory")
public class WardHistory {

    @Id
    private String historyId;
    private String patientId;
    private String wardNo;
    private String wardType;
    private OffsetDateTime inDate;
    private OffsetDateTime outDate;

}