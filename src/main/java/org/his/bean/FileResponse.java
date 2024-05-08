package org.his.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileResponse {

    private byte[] content;
    private Resource resource;
    private String stringContent;
    private String error;

}