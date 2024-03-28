package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PersonalDetail;
import org.his.bean.PersonalDetailResp;
import org.his.bean.ScheduleDetail;
import org.his.bean.ScheduleDetailResp;
import org.his.config.Roles;
import org.his.entity.user.Admin;
import org.his.entity.user.Doctor;
import org.his.entity.user.Nurse;
import org.his.exception.AuthenticationException;
import org.his.repo.LoginRepo;
import org.his.repo.user.AdminRepo;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.NurseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CommonService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private NurseRepo nurseRepo;

    @Autowired
    private LoginRepo loginRepo;

    public PersonalDetailResp getPersonalDetail(String id, String role){
        PersonalDetailResp resp = new PersonalDetailResp();

        if(Roles.DOCTOR.toString().equals(role)){
            Optional<Admin> obj = adminRepo.findById(id);
            if(obj.isEmpty()){
                resp.setError("Username not found");
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

    public ScheduleDetailResp getScheduleDetail(String email, String role) {
        ScheduleDetailResp resp = new ScheduleDetailResp();
        String msg = null;
        try{
            if(email==null || email.isEmpty() || role==null || role.isEmpty()){
                msg = "Please pass valid email/role.";
                throw new AuthenticationException("Empty value receives in request");
            }

            Optional<String> optUserId = loginRepo.findUserIdByUsername(email, role);
            if (optUserId.isEmpty()) {
                msg = "Email not found in the database.";
                throw new AuthenticationException(msg);
            }

            String userId = optUserId.get();
            ScheduleDetail detail = null;
            if(Roles.DOCTOR.toString().equals(role)){
                Optional<Doctor> doc = doctorRepo.findById(userId);
                detail = getScheduleDetailForDoc(doc.get(), email);
            } else if (Roles.NURSE.toString().equals(role)) {
                Optional<Nurse> nurse = nurseRepo.findById(userId);
                detail = getScheduleDetailForNurse(nurse.get(), email);

            } else{
                msg = "Pass correct ROLE of the user.";
                throw new Exception("Role other than doctor or nurse has been passed in request.");
            }
            resp.setResponse(detail);
            return resp;

        } catch (AuthenticationException e) {
            log.error("AuthenticationException occurred with msg : " + e.getMessage());
        } catch (Exception e){
            log.error("Exception occurred with msg : " + e.getMessage());
        }
        resp.setError(msg);
        return resp;
    }

    private ScheduleDetail getScheduleDetailForNurse(Nurse nurse, String email) {
        ScheduleDetail detail = new ScheduleDetail();
        detail.setFirstName(nurse.getFirstName());
        detail.setLastName(nurse.getLastName());
        detail.setEmail(email);
        detail.setUserId(nurse.getId());
        detail.setRole("NURSE");
        detail.setMon(nurse.getMon());
        detail.setTue(nurse.getTue());
        detail.setWed(nurse.getWed());
        detail.setThu(nurse.getThu());
        detail.setFri(nurse.getFri());
        detail.setSat(nurse.getSat());
        detail.setSun(nurse.getSun());
        return detail;
    }

    private ScheduleDetail getScheduleDetailForDoc(Doctor doctor, String email) {
        ScheduleDetail detail = new ScheduleDetail();
        detail.setFirstName(doctor.getFirstName());
        detail.setLastName(doctor.getLastName());
        detail.setEmail(email);
        detail.setUserId(doctor.getId());
        detail.setRole("DOCTOR");
        detail.setMon(doctor.getMon());
        detail.setTue(doctor.getTue());
        detail.setWed(doctor.getWed());
        detail.setThu(doctor.getThu());
        detail.setFri(doctor.getFri());
        detail.setSat(doctor.getSat());
        detail.setSun(doctor.getSun());
        return detail;
    }
}