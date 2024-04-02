package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrescriptionItem {

    private String name;
    private String diagnosisId;
    private String date;
    private Map<String, Integer> medicine;
    //Add other fields as needed

}