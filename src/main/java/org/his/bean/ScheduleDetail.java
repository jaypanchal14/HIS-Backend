package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDetail extends Shift{

    private String firstName;
    private String lastName;
    private String email;
    private String userId;
    private String role;
//    private int mon;
//    private int tue;
//    private int wed;
//    private int thu;
//    private int fri;
//    private int sat;
//    private int sun;

}