package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.AuthRequest;
import org.his.bean.AuthResponse;
import org.his.bean.GeneralResp;
import org.his.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request){
        AuthResponse resp;
        resp = loginService.authenticate(request);
        if(resp.getResponse().equals("SUCCESS")){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(resp);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<AuthResponse> changePassword(@RequestBody AuthRequest request){
        AuthResponse resp;
        resp = loginService.changePassword(request);
        if(resp.getResponse().equals("SUCCESS")){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(resp);
        }
    }

    @GetMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam(name="email") String email){
        log.info("Request received for forgot-password");
        GeneralResp resp = loginService.forgotPassword(email);
        if(resp.getResponse().equals("SUCCESS")){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(resp);
        }
    }

}