package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {

    private PersonalDetail detail;

    private Shift shift;
    //To display count only if doctor/nurse is on-duty. (below three fields are only for doctor and nurse)
    private int onDuty;
    private int ipPatient;
    private int opPatient;

    //Only used by admin
    private Map<String, Long> count;

    private String error;

}