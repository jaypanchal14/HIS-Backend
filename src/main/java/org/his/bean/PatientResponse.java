package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponse {

    private List<PatientDetail> response;
    private String error;

}