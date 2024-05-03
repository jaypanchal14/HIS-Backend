package org.his.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Emergency")
public class Emergency {

    @Id
    @Column(length = 30)
    private String emerId;
    @Column(length = 40)
    private String doctorId;
    private boolean handled;
    @Column(length = 1024)
    private String remark;
    @UpdateTimestamp
    private OffsetDateTime date;
}