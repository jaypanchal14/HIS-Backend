package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.Error;
import org.his.bean.PersonalDetail;
import org.his.bean.PersonalDetailResp;
import org.his.config.Roles;
import org.his.entity.user.Admin;
import org.his.repo.user.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CommonService {

    @Autowired
    private AdminRepo adminRepo;

    public PersonalDetailResp getPersonalDetail(String id, String role){
        PersonalDetailResp resp = new PersonalDetailResp();

        if(Roles.DOCTOR.toString().equals(role)){
            Optional<Admin> obj = adminRepo.findById(id);
            if(obj.isEmpty()){
                Error err = new Error();
                err.setMsg("Username not found.");
                resp.setError(err);
                return resp;
            }
            PersonalDetail detail = getDetailFromBean(obj.get());
            resp.setResponse(detail);
        }

        return resp;
    }

    private PersonalDetail getDetailFromBean(Admin admin) {
        PersonalDetail obj = new PersonalDetail();
        obj.setFirstName(admin.getFirstName());

        /*Add remaining variables*/

        return obj;
    }

}