package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/admin/updateSchedule")
    public ResponseEntity<?> updateSchedule(@RequestBody ScheduleDetail request){
        log.info("Request received for updating schedule");
        GeneralResp resp = adminService.updateSchedule(request);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/admin/checkUser")
    public ResponseEntity<?> checkUser(@RequestBody CheckUserReq request){
        log.info("Request received to check if user exist or not");
        PersonalDetailResp resp = adminService.checkIfUserExist(request);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/admin/updateAccountStatus")
    public ResponseEntity<?> updateAccountStatus(@RequestBody UpdateAccStatusReq request){
        log.info("Request received to check if user exist or not");
        GeneralResp resp = adminService.updateAccountStatus(request);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/admin/getCount")
    public ResponseEntity<?> countActiveUserByRole(@RequestParam(name="id") String id){
        log.info("Request received to count active users.");
        RoleCountResp resp = adminService.getActiveUserByRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/admin/addUser")
    public ResponseEntity<?> addUser(@RequestBody NewUserRequest request, @RequestParam String adminId){
        log.info("Request received to add new user by admin.");
        GeneralResp resp = adminService.addNewUser(request, adminId);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

}