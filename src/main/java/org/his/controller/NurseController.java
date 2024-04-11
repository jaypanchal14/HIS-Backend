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

    private final NurseService nurseService;

    @Autowired
    public NurseController(NurseService nurseService) {
        this.nurseService = nurseService;
    }

    @GetMapping("/nurse/onShiftNurse")
    public ResponseEntity<ReceptionDetailResp> onShiftNurse(
            @RequestParam("role") String role,
            @RequestParam("id") String id
    ) {
        ReceptionDetailResp response = nurseService.getOnShiftNurses(id, role);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/nurse/getWard")
    public ResponseEntity<WardResponse> getWardDetails(
            @RequestParam("role") String role,
            @RequestParam("id") String id
    ) {
        WardResponse response = nurseService.getWardDetails(id, role);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/nurse/updateWard/{nurseId}")
    public ResponseEntity<GeneralResp> updateWard (
            @PathVariable("nurseId") String nurseId,
            @RequestBody PatientDetail patientDetail
    ) {
        GeneralResp response = nurseService.updateWard(patientDetail, nurseId);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
