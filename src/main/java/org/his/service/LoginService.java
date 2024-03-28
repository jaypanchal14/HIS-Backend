package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.AuthRequest;
import org.his.bean.AuthResponse;
import org.his.entity.Login;
import org.his.exception.AuthenticationException;
import org.his.repo.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private LoginRepo loginRepo;

    public void getAccount() {
        Optional<Login> u = loginRepo.findAccountByUsername("his.team40@gmail.com","DOCTOR");
        u.ifPresent(l -> System.out.println(l.toString()));
    }

    public void createAccount() {

    }

    public AuthResponse authenticate(AuthRequest request) {
        AuthResponse resp = new AuthResponse();
        String msg = null;
        try {
            if (validateAuthRequest(request)) {
                msg = "Please pass valid values in request.";
                throw new AuthenticationException("Empty values passed in the request");
            }
            //Optional<Login> optAccount = loginRepo.findById(request.getUsername());
            Optional<Login> optAccount = loginRepo.findAccountByUsername(request.getUsername(), request.getRole());
            if (optAccount.isEmpty()) {
                msg = "Username not found in the database.";
                throw new AuthenticationException(msg);
            }

            Login account = optAccount.get();
            if (request.getPassword().equals(account.getPassword())) {
                if (account.isActive()) {
                    log.info("User authenticated successfully.");
                    //resp.setToken(getJWTToken(optAccount.get()));
                    resp.setResponse("SUCCESS");
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

        resp.setError(msg);
        log.error("User authenticated failure.");
        return resp;

    }

    public AuthResponse changePassword(AuthRequest request) {
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

        resp.setError(msg);
        log.error("ChangePassword request failed.");
        return resp;
    }


    private boolean validateAuthRequest(AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return true;
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return true;
        }
        return request.getRole() == null || request.getRole().isEmpty();
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

}