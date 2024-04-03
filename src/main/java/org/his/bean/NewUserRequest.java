package org.his.bean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewUserRequest {

    @Valid
    private PersonalDetail personal;
    @JsonIgnore
    private Shift shift;

}