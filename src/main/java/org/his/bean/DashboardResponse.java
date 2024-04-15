package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {

    private PersonalDetail detail;
    private Shift shift;
    //To display count only if user is on-duty.
    private int onDuty;
    private int ipPatient;
    private int opPatient;

    private String error;

}