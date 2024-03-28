package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.AuthRequest;
import org.his.bean.AuthResponse;
import org.his.service.LoginService;
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
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request){
        AuthResponse resp;
        resp = loginService.authenticate(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resp);
    }

}