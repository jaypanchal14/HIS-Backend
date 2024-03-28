package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.GeneralResp;
import org.his.bean.ScheduleDetail;
import org.his.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/his")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/admin/updateSchedule")
    public ResponseEntity<?> updateSchedule(@RequestBody ScheduleDetail request){
        log.info("Request received for updating schedule");
        GeneralResp resp = adminService.updateSchedule(request);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

}