package org.his.bean;

import lombok.Data;

import java.util.List;

@Data
public class PatientResponse {

    private List<PatientDetail> response;
    private String error;

}