package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WardDetail {

    private String wardNo;
    private String type;
    private boolean isEmpty;
    private String patientId;
    private String firstName;
    private String lastName;

    private String date;
}
