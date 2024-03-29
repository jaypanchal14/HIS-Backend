package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.GeneralResp;
import org.his.bean.ScheduleDetail;
import org.his.config.Roles;
import org.his.entity.user.Doctor;
import org.his.entity.user.Nurse;
import org.his.exception.NoSuchAccountException;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.NurseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private NurseRepo nurseRepo;

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
}