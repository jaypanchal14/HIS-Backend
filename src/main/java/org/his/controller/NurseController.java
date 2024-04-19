package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class NurseController {

    @Autowired
    private NurseService nurseService;

    @GetMapping("/nurse/home")
    public ResponseEntity<?> dashboard(@RequestParam(name = "userId") String userId){
        log.info("dashboard | request received to get dashboard-data");
        DashboardResponse resp = nurseService.getDashBoard(userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/nurse/onShiftNurse")
    public ResponseEntity<ReceptionDetailResp> onShiftNurse(@RequestParam("userId") String userId) {
        ReceptionDetailResp resp = nurseService.getOnShiftNurses(userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/nurse/getWard")
    public ResponseEntity<WardResponse> getWardDetails(@RequestParam("userId") String userId) {
        WardResponse resp = nurseService.getWardDetails(userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/nurse/updateWard")
    public ResponseEntity<GeneralResp> updateWard (@RequestParam("nurseId") String nurseId, @RequestBody PatientDetail patientDetail) {
        GeneralResp resp = nurseService.updateWard(patientDetail, nurseId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
