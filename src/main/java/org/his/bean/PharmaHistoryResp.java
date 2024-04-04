package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PharmaHistoryResp {

    private List<PrescriptionItem> response;
    private String error;

}