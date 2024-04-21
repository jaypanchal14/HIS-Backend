package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.DiagnosisResponse;
import org.his.bean.GeneralResp;
import org.his.bean.OnePatientResponse;
import org.his.bean.PatientResponse;
import org.his.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class PatientController {

    @Autowired
    private PatientService patientService;

    //Will be called by Doctor and Nurse to get IP and OP patients
    @GetMapping("/patient/viewLivePatients")
    public ResponseEntity<PatientResponse> viewLivePatients(
            @RequestParam(name="role") String role,
            @RequestParam(name="isOP") int isOP,
            @RequestParam(name="userId") String userId
    ){
        log.info("viewLivePatients | Request received for getting active patients");
        log.info("user: "+userId+", role: "+role+", isOP: "+isOP );
        PatientResponse resp = patientService.viewLivePatients(role, userId, isOP);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/patient/viewOneLivePatient")
    public ResponseEntity<?> viewOneLivePatient(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "admitId") String admitId
    ) {
        log.info("viewOneLivePatient | Request received for getting one live patients.");

        OnePatientResponse resp = patientService.viewOneLivePatient(role, userId, admitId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/patient/addDiagnosis")
    public ResponseEntity<?> addDiagnosis(@RequestPart(name = "file", required = false) MultipartFile file,
                                          @RequestParam(name = "request") String request,
                                          @RequestParam(name = "role") String role,
                                          @RequestParam(name = "userId") String userId
    ) {
        log.info("addDiagnosis | request received to add new diagnosis");
        GeneralResp resp = patientService.addDiagnosis(request, file, role, userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/patient/getDiagnosisWithAdmitId")
    public ResponseEntity<?> getDiagnosisWithAdmitId(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "admitId") String admitId,
            @RequestParam(name = "userId") String userId
    ) {

        log.info("getDiagnosisWithAdmitId | request received.");
        DiagnosisResponse resp = patientService.getDiagnosisForAdmitId(role, admitId, userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/patient/pastHistory")
    public ResponseEntity<?> pastHistory(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "patientId", required = false) String patientId
    ) {

        log.info("pastHistory | request received to view past-history");
        PatientResponse  resp = patientService.pastHistory(role, userId, patientId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/patient/pastHistoryOnePatient")
    public ResponseEntity<?> pastHistoryOnePatient(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "patientId") String patientId,
            @RequestParam(name = "userId") String userId
    ) {

        log.info("pastHistoryOnePatient | request received to view past-history of a patient");
        OnePatientResponse resp = patientService.pastHistoryOnePatient(role, patientId, userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

}