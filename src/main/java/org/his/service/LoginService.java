package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.AuthRequest;
import org.his.bean.AuthResponse;
import org.his.bean.GeneralResp;
import org.his.entity.Login;
import org.his.exception.AuthenticationException;
import org.his.repo.LoginRepo;
import org.his.security.JwtUtils;
import org.his.util.EmailService;
import org.his.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse authenticate(AuthRequest request) {
        AuthResponse resp = new AuthResponse();
        String msg = null;
        try {
            if (validateAuthRequest(request)) {
                msg = "Please pass valid values in request.";
                throw new AuthenticationException("Empty values passed in the request");
            }
            Optional<Login> optAccount = loginRepo.findById(request.getUsername());
//            Optional<Login> optAccount = loginRepo.findAccountByUsername(request.getUsername(), request.getRole());
            if (optAccount.isEmpty()) {
                msg = "Username not found in the database.";
                throw new AuthenticationException(msg);
            }

            Login account = optAccount.get();
            if (request.getPassword().equals(account.getPassword())) {
                if (account.isActive()) {
                    log.info("User authenticated successfully.");
                    resp.setToken(jwtUtils.generateToken(request.getUsername(), Collections.singletonList(request.getRole())));
                    resp.setResponse("SUCCESS");
                    resp.setRole(account.getRole());
                    resp.setUserId(account.getUserId());
                    return resp;
                } else {
                    msg = "User account is blocked by ADMIN.";
                }

            } else {
                msg = "Username OR Password is incorrect.";
            }

        } catch (AuthenticationException e) {
            log.error("AuthenticationException occurred with msg : " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception occurred with msg : " + e.getMessage());
        }
        resp.setResponse("FAILED");
        resp.setError(msg);
        log.error("User authentication failure : "+request);
        return resp;

    }

    public AuthResponse changePassword(AuthRequest request) {
        log.info("Request: "+request);
        AuthResponse resp = new AuthResponse();
        String msg = null;
        try {
            if (validateChangePass(request)) {
                msg = "Please pass valid values in request.";
                throw new AuthenticationException("Empty values passed in the request");
            }

            Optional<Login> optAccount = loginRepo.findAccountByUserId(request.getUserId(), request.getRole());
            if (optAccount.isEmpty()) {
                msg = "User not found in the database.";
                throw new AuthenticationException(msg);
            }

            Login account = optAccount.get();
            if (request.getOldPassword().equals(account.getPassword())) {
                if (account.isActive()) {
                    account.setPassword(request.getNewPassword());
                    Integer count = loginRepo.updatePassword(request.getUserId(), request.getNewPassword());
                    log.info("Password updated successfully for account : "+count);
                    resp.setResponse("SUCCESS");
                    resp.setUserId(account.getUserId());
                    return resp;
                } else {
                    msg = "User account is blocked by ADMIN.";
                }

            } else {
                msg = "Wrong Old-password provided in the request.";
            }

        } catch (AuthenticationException e) {
            log.error("AuthenticationException occurred with msg : " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception occurred with msg : " + e.getMessage());
        }
        resp.setResponse("FAILED");
        resp.setError(msg);
        log.error("ChangePassword request failed.");
        return resp;
    }


    private boolean validateAuthRequest(AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return true;
        }
        return request.getPassword() == null || request.getPassword().isEmpty();
        //return request.getRole() == null || request.getRole().isEmpty();
    }

    private boolean validateChangePass(AuthRequest request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return true;
        }
        if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
            return true;
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return true;
        }
        return request.getRole() == null || request.getRole().isEmpty();
    }

    public GeneralResp forgotPassword(String email) {
        GeneralResp resp = new GeneralResp();
        String msg = null;
        try{
            if(email == null || email.isEmpty()){
                throw new Exception("Empty email address passed.");
            }
            Optional<Login> optAccount = loginRepo.findById(email);
            if (optAccount.isEmpty()) {
                msg = "Username not found in the database.";
                throw new AuthenticationException(msg);
            }

            Login account = optAccount.get();
            String newPass = Utility.generateRandomPassword(8);
            Integer count = loginRepo.updatePassword(account.getUserId(), newPass);
            if(count==0){
                msg = "Unable to update the password in database for the user";
                throw new Exception(msg);
            }
            if(emailService.sendEmailWithPassword(email, newPass)){
                log.info("Email sent to the user with new password.");
            }else{
                log.warn("Got exception while sending email to the user with new password.");
            }
            log.info("Password updated successfully for account : "+count);
            resp.setResponse("SUCCESS");
        } catch (Exception e){
            log.error("Exception occurred while forgotPassword: "+e.getMessage());
            resp.setError(msg);
            resp.setResponse("FAILED");
        }
        return resp;
    }
}