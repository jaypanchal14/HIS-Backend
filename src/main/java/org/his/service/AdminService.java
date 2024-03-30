package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.config.Roles;
import org.his.entity.Login;
import org.his.entity.user.*;
import org.his.exception.AuthenticationException;
import org.his.exception.NoSuchAccountException;
import org.his.repo.LoginRepo;
import org.his.repo.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private NurseRepo nurseRepo;

    @Autowired
    private PharmaRepo pharmaRepo;

    @Autowired
    private ReceptionistRepo receptionRepo;

    @Autowired
    private LoginRepo loginRepo;

    public GeneralResp updateSchedule(ScheduleDetail request) {
        GeneralResp resp = new GeneralResp();
        String msg = null;
        try{
            if(validateScheduleRequest(request)){
                throw new Exception("Wrong request parameter values.");
            }
            if("DOCTOR".equals(request.getRole())){
                Optional<Doctor> optDoc = doctorRepo.findById(request.getUserId());
                if(optDoc.isEmpty()){
                    msg = "User not found.";
                    throw new NoSuchAccountException(msg);
                }
                updateScheduleInDoctorTable(optDoc.get(), request);
            } else if ("NURSE".equals(request.getRole())) {
                Optional<Nurse> optNurse = nurseRepo.findById(request.getUserId());
                if(optNurse.isEmpty()){
                    msg = "User not found.";
                    throw new NoSuchAccountException(msg);
                }
                updateScheduleInNurseTable(optNurse.get(), request);
            }else{
                msg = "Pass correct ROLE of the user.";
                throw new Exception("Role other than doctor or nurse has been passed in request.");
            }

        } catch (NoSuchAccountException e){
            log.error("NoSuchAccountException occurred with msg : " + e.getMessage());

        } catch (Exception e){
            log.error("Exception occurred with msg : " + e.getMessage());

        }
        resp.setError(msg);
        return resp;
    }

    private void updateScheduleInNurseTable(Nurse nurse, ScheduleDetail request) {
        nurse.setMon(request.getMon());
        nurse.setTue(request.getTue());
        nurse.setWed(request.getWed());
        nurse.setThu(request.getThu());
        nurse.setFri(request.getFri());
        nurse.setSat(request.getSat());
        nurse.setSun(request.getSun());
        nurseRepo.save(nurse);
    }

    private void updateScheduleInDoctorTable(Doctor doctor, ScheduleDetail request) {
        doctor.setMon(request.getMon());
        doctor.setTue(request.getTue());
        doctor.setWed(request.getWed());
        doctor.setThu(request.getThu());
        doctor.setFri(request.getFri());
        doctor.setSat(request.getSat());
        doctor.setSun(request.getSun());
        doctorRepo.save(doctor);
    }

    private boolean validateScheduleRequest(ScheduleDetail request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return true;
        }
        if (request.getRole() == null || request.getRole().isEmpty()){
            return true;
        }
        if(request.getMon()<0 || request.getMon()>3){
            return true;
        }
        if(request.getTue()<0 || request.getTue()>3){
            return true;
        }
        if(request.getWed()<0 || request.getWed()>3){
            return true;
        }
        if(request.getThu()<0 || request.getThu()>3){
            return true;
        }
        if(request.getFri()<0 || request.getFri()>3){
            return true;
        }
        if(request.getSat()<0 || request.getSat()>3){
            return true;
        }
        return request.getSun() < 0 || request.getSun() > 3;
    }

    public PersonalDetailResp checkIfUserExist(CheckUserReq request) {
        PersonalDetailResp resp = new PersonalDetailResp();
        String msg = null;
        try{
            //Check if admin is present

            //Retrieve details based on email-id and role
            Optional<Login> optAccount = loginRepo.findById(request.getEmail());
            if (optAccount.isEmpty()) {
                msg = "Username not found in the database.";
                throw new AuthenticationException(msg);
            }

            Login account = optAccount.get();
            switch(request.getRole()) {
                case "NURSE":
                    Optional<Nurse> optNurse = nurseRepo.findById(account.getUserId());
                    if (optNurse.isEmpty()) {
                        resp.setError("Nurse not found in database");
                    }else{
                        Nurse n = optNurse.get();
                        PersonalDetail detail = new PersonalDetail();
                        detail.setFirstName(n.getFirstName());
                        detail.setLastName(n.getLastName());
                        detail.setEmail(request.getEmail());
                        detail.setActive(account.isActive());
                        detail.setUserId(account.getUserId());
                        resp.setResponse(detail);
                    }
                    break;

                case "DOCTOR":
                    Optional<Doctor> optDoc = doctorRepo.findById(account.getUserId());
                    if (optDoc.isEmpty()) {
                        resp.setError("Doctor not found in database");
                    }else{
                        Doctor d = optDoc.get();
                        PersonalDetail detail = new PersonalDetail();
                        detail.setFirstName(d.getFirstName());
                        detail.setLastName(d.getLastName());
                        detail.setEmail(request.getEmail());
                        detail.setUserId(account.getUserId());
                        detail.setActive(account.isActive());
                        resp.setResponse(detail);
                    }
                    break;

                case "PHARMACIST":
                    Optional<Pharma> optPharma = pharmaRepo.findById(account.getUserId());
                    if (optPharma.isEmpty()) {
                        resp.setError("Username not found");
                    }else{
                        Pharma p = optPharma.get();
                        PersonalDetail detail = new PersonalDetail();
                        detail.setFirstName(p.getFirstName());
                        detail.setLastName(p.getLastName());
                        detail.setEmail(request.getEmail());
                        detail.setUserId(account.getUserId());
                        detail.setActive(account.isActive());
                        resp.setResponse(detail);
                    }
                    break;

                case "RECEPTIONIST":
                    Optional<Receptionist> optReception = receptionRepo.findById(account.getUserId());
                    if (optReception.isEmpty()) {
                        resp.setError("Username not found");
                    }else{
                        Receptionist r = optReception.get();
                        PersonalDetail detail = new PersonalDetail();
                        detail.setFirstName(r.getFirstName());
                        detail.setLastName(r.getLastName());
                        detail.setEmail(request.getEmail());
                        detail.setUserId(account.getUserId());
                        detail.setActive(account.isActive());
                        resp.setResponse(detail);
                    }
                    break;

                default:
                    msg = "Undefined role passed in the request.";
                    log.error(msg);
                    resp.setError(msg);
            }

        } catch (Exception e){
            log.error("Exception occurred with msg : "+e.getMessage());
            resp.setError(msg);
        }
        return resp;
    }

    public GeneralResp updateAccountStatus(UpdateAccStatusReq request) {
        GeneralResp resp = new GeneralResp();
        String msg = null;
        try{
            //Check if admin is allowed to perform this operation or not
            Optional<Login> optAccount = loginRepo.findAccountByUserId(request.getAdminId(), "ADMIN");
            if (optAccount.isEmpty()) {
                msg = "Not allowed to perform this request";
                throw new AuthenticationException("User doesn't have the privilege to perform this request.");
            }

            //Check if the user is present in the database or not
            optAccount = loginRepo.findAccountByUserId(request.getUserId(), request.getRole());
            if (optAccount.isEmpty()) {
                msg = "No such user exists in the database to modify status.";
                throw new AuthenticationException(msg);
            }

            if("A".equals(request.getAction())){
                int res = loginRepo.updateAccountStatus( request.getUserId(), true);
                if(res==0){
                    throw new Exception("Failed while updating the status");
                }
            }else if("D".equals(request.getAction())){
                int res = loginRepo.updateAccountStatus( request.getUserId(), false);
                if(res==0){
                    throw new Exception("Failed while updating the status");
                }
            }else{
                throw new Exception("Wrong action code passed in the request.");
            }
            resp.setResponse("SUCCESS");
        } catch(AuthenticationException e){
            log.error("AuthenticationException occurred while updating the status: "+e.getMessage());
            resp.setError(msg);
        } catch(Exception e){
            log.error("Exception occurred while updating the status: "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public RoleCountResp getActiveUserByRole(String adminId) {
        RoleCountResp resp = new RoleCountResp();
        String msg = null;
        try{
            if(adminId == null || adminId.isEmpty()){
                msg = "Empty adminId passed in the request.";
                throw new AuthenticationException("Empty adminId passed");
            }

            Optional<Login> optAccount = loginRepo.findAccountByUserId(adminId, "ADMIN");
            if (optAccount.isEmpty()) {
                msg = "Not allowed to perform this request";
                throw new AuthenticationException("User doesn't have the privilege to perform this request.");
            }

            List<RoleCount> ls = loginRepo.countActiveUserByRole();
            //resp.setResponse(ls);
            Map<String, Long> m = new HashMap<>();
            for(RoleCount rc : ls){
                m.put(rc.getRole(), rc.getCount());
            }
            resp.setResponse(m);
        } catch (AuthenticationException e){
            log.error("AuthenticationException occurred while getting active user count: "+e.getMessage());
            resp.setError(msg);
        } catch (Exception e){
            log.error("Exception occurred while getting active user count: "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }
}