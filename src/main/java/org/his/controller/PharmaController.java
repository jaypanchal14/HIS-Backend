package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PharmaHistoryResp;
import org.his.service.PharmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class PharmaController {

    @Autowired
    private PharmaService pharmaService;

    @GetMapping("/pharma/viewHistory")
    public ResponseEntity<?> getHistory(
            @RequestParam(name = "pharmaId")  String pharmaId,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate",   required = false) String endDate
    ){

        log.info("getHistory | Request received for viewing pharma-history");
        PharmaHistoryResp resp = pharmaService.getPharmaHistory(pharmaId, startDate, endDate);
        if (resp.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }
    }

    @GetMapping("/pharma/viewByDiagnosis")
    public ResponseEntity<?> getSpecificDiagnosis(
            @RequestParam(name = "pharmaId")    String pharmaId,
            @RequestParam(name = "diagnosisId") String diagnosisId
    ){
        log.info("getSpecificDiagnosis | Request received for viewing prescription based on diagnosisId.");
        PharmaHistoryResp resp = pharmaService.getOneFromDiagnosisId(pharmaId, diagnosisId);
        if (resp.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }
    }

}