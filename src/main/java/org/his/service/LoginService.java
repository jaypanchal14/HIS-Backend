package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.AuthRequest;
import org.his.bean.AuthResponse;
import org.his.bean.Error;
import org.his.entity.Login;
import org.his.repo.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private LoginRepo loginRepo;

    public void getAccount(){
        Optional<Login> u = loginRepo.findAccountByUsername("his.team40@gmail.com");
        u.ifPresent(l -> System.out.println(l.toString()));
    }

    public void createAccount(){

    }

    public AuthResponse authenticate(AuthRequest request){
        AuthResponse resp = new AuthResponse();
        if(validateRequest(request)){
            Error err = new Error();
            err.setMsg("Please pass valid values in request.");
            resp.setResponse("FAILED");
            resp.setError(err);
            log.error("Values provided in request are not in correct format.");
            return resp;
        }

        Optional<Login> optAccount = loginRepo.findAccountByUsername(request.getUsername());
        if(optAccount.isEmpty() ){
            Error err = new Error();
            err.setMsg("Username not found.");
            resp.setResponse("FAILED");
            resp.setError(err);
            log.error("Username not found.");
            return resp;
        }
        Login account = optAccount.get();
        String msg;
        if(request.getPassword().equals(account.getPassword())){

            if(request.getRole().equals(account.getRole()) ){

                if(account.isActive()) {
                    log.info("User authenticated successfully.");
                    //resp.setToken(getJWTToken(optAccount.get()));
                    resp.setResponse("SUCCESS");
                    resp.setUserId(account.getUserId());
                    return resp;
                }else{
                    msg = "User account is blocked by ADMIN.";
                }

            }else{
                msg = "User is not authorized for this role";
            }

        }else{
            msg = "Username OR Password is incorrect.";
        }
        Error err = new Error();
        err.setMsg(msg);
        resp.setResponse("FAILED");
        resp.setError(err);
        log.error("User authenticated failure.");
        return resp;

    }

    private boolean validateRequest(AuthRequest request) {
        if(request.getUsername() == null || request.getUsername().isEmpty()){
            return true;
        }
        if(request.getPassword() == null || request.getPassword().isEmpty()){
            return true;
        }
        return request.getRole() == null || request.getRole().isEmpty();
    }

}