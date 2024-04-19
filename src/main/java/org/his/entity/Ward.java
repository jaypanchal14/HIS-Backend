package org.his.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Ward")
public class Ward {

    @Id
    private String wardId;
    private String wardNo;
    private String wardType;
    private boolean isEmpty;
    private String patientId;
    private String firstName;
    private String lastName;
    @UpdateTimestamp
    private OffsetDateTime date;

}