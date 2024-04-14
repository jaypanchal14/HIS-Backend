package org.his.bean;

import lombok.Data;

@Data
public class DashboardResponse {

    private PersonalDetail detail;
    private Shift shift;
    //To display count only if user is on-duty.
    private int onDuty;
    private int ipPatient;
    private int opPatient;

    private String error;

}