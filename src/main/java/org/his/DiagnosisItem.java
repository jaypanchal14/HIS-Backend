package org.his;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiagnosisItem {

    private String patientId;
    private String admitId;
    private String remarks;
    private int discharge;
    Map<String, Integer> medicine;

    //Below fields used in DiagnosisResponse class
    private String diagnosisId;
    private String file;
    private String date;

}