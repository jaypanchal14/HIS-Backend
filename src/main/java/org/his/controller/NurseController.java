package org.his.controller;
import lombok.extern.slf4j.Slf4j;
import org.his.bean.GeneralResp;
import org.his.bean.PatientDetail;
import org.his.bean.ReceptionDetailResp;
import org.his.bean.WardResponse;
import org.his.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class NurseController {

    @Autowired
    private NurseService nurseService;

    @GetMapping("/nurse/onShiftNurse")
    public ResponseEntity<ReceptionDetailResp> onShiftNurse(
            @RequestParam("role") String role,
            @RequestParam("id") String id
    ) {
        ReceptionDetailResp resp = nurseService.getOnShiftNurses(id, role);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/nurse/getWard")
    public ResponseEntity<WardResponse> getWardDetails(
            @RequestParam("role") String role,
            @RequestParam("id") String id
    ) {
        WardResponse resp = nurseService.getWardDetails(id, role);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/nurse/updateWard/{nurseId}")
    public ResponseEntity<GeneralResp> updateWard (
            @PathVariable("nurseId") String nurseId,
            @RequestBody PatientDetail patientDetail
    ) {
        GeneralResp resp = nurseService.updateWard(patientDetail, nurseId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
