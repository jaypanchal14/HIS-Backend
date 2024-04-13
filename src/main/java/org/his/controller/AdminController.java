package org.his.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.exception.NoSuchAccountException;
import org.his.service.AdminService;
import org.his.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/admin/updateSchedule")
    public ResponseEntity<?> updateSchedule(@RequestBody ScheduleDetail request){
        log.info("updateSchedule | Request received for updating schedule");
        GeneralResp resp = adminService.updateSchedule(request);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/admin/checkUser")
    public ResponseEntity<?> checkUser(@RequestBody CheckUserReq request){
        log.info("checkUser | Request received to check if user exist or not");
        PersonalDetailResp resp = adminService.checkIfUserExist(request);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/admin/updateAccountStatus")
    public ResponseEntity<?> updateAccountStatus(@RequestBody UpdateAccStatusReq request){
        log.info("updateAccountStatus | Request received to update user-account status");
        GeneralResp resp = adminService.updateAccountStatus(request);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/admin/getCount")
    public ResponseEntity<?> countActiveUserByRole(@RequestParam(name="id") String id){
        log.info("countActiveUserByRole | Request received to count active users.");
        RoleCountResp resp = adminService.getActiveUserByRole(id);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping(path = "/admin/addUser")
    public ResponseEntity<?> addUser(@RequestPart(name = "image")MultipartFile image, @RequestParam(name = "request") String request){
        log.info("addUser | Request received to add new user by admin.");
        GeneralResp resp = adminService.addNewUser(request, image);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/admin/viewUsers")
    public ResponseEntity<?> viewUsers(@RequestParam(required = false) String role){
        log.info("viewUsers | Request received for viewing the users.");
        ViewUserResponse resp = adminService.getUsers(role);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/tryimage")
    public ResponseEntity<?> tryImage(){
        String resp = adminService.tryImage();
        if(resp == null || resp.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/admin/blah")
    public ResponseEntity<?> blahBlah() throws NoSuchAccountException {
        throw new NoSuchAccountException("Le lo bhai test karne");
    }

}