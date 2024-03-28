package org.his.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "Login")
public class Login {

    //username would be the user-email (unique)
    @Id
    private String username;
    private String password;
    //Used internally for processing (Only for operation, not used by user explicitly)
    private String userId;
    private String role;
    private boolean isActive;
    private OffsetDateTime updatedAt;

}
