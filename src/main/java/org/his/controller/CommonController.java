package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PersonalDetailResp;
import org.his.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/his")
public class CommonController {

    @Autowired
    private CommonService service;

    @GetMapping("/personalDetails")
    public ResponseEntity<?> getPersonalDetails(@RequestParam(name="id") String id, @RequestParam(name="role") String role){
        log.info("Request received for getting personal details.");
        PersonalDetailResp resp = service.getPersonalDetail(id, role);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

}