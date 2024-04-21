package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnePatientResponse {

    private PatientDetail detail;
    private List<DiagnosisItem> list;
    private String error;

}