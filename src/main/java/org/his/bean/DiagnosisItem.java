package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagnosisItem {

    private String patientId;
    private String admitId;
    private String remarks;
    private int discharge;
    private Map<String, Integer> medicine;

    //Below fields used in DiagnosisResponse class
    private String diagnosisId;
    private String file;
    private String date;

}