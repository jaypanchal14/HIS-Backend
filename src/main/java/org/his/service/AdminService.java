package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.entity.Login;
import org.his.entity.user.*;
import org.his.exception.AuthenticationException;
import org.his.exception.NoSuchAccountException;
import org.his.repo.LoginRepo;
import org.his.repo.user.*;
import org.his.util.EmailService;
import org.his.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.OffsetDateTime;
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

    @Autowired
    private EmailService emailService;

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
        try{

            validateAdminId(adminId);

            List<RoleCount> ls = loginRepo.countActiveUserByRole();
            //resp.setResponse(ls);
            Map<String, Long> m = new HashMap<>();
            for(RoleCount rc : ls){
                m.put(rc.getRole(), rc.getCount());
            }
            resp.setResponse(m);
        } catch (AuthenticationException e){
            log.error("AuthenticationException occurred while getting active user count: "+e.getMessage());
            resp.setError(e.getMessage());
        } catch (Exception e){
            log.error("Exception occurred while getting active user count: "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }

    private void validateAdminId(String adminId) throws AuthenticationException {
        if(adminId == null || adminId.isEmpty()){
            throw new AuthenticationException("Empty adminId passed in the request.");
        }

        Optional<Login> optAccount = loginRepo.checkIfUserIsActive(adminId, "ADMIN");
        if (optAccount.isEmpty()) {
            throw new AuthenticationException("User doesn't have the privilege to perform this request.");
        }
    }

    public GeneralResp addNewUser(NewUserRequest request) {
        GeneralResp resp = new GeneralResp();
        try {
            log.info(request.toString());
            //Going further, it will be handled by JWT
            //validateAdminId(adminId);

            //Validate request, by checking mandatory field value
            validateNewUserRequest(request);

            //Check if user is already inserted with same email-id
            Optional<Login> optionalAcc = loginRepo.findById(request.getPersonal().getEmail());
            if(optionalAcc.isPresent()){
                throw new Exception("Email-id is already being used, please use another email-id.");
            }

            //Prepare beans for respective user
            Login account = getNewAccountFromRequest(request.getPersonal());
            //Add entry in login table
            //Add entry in specific user-table
            switch (request.getPersonal().getRole().toUpperCase()){
                case "DOCTOR" : {
                    Doctor doc = getNewDoctorFromRequest(account, request.getPersonal(), request.getShift());
                    insertNewDoctor(account, doc);
                    break;
                }
                case "NURSE" : {
                    Nurse nur = getNewNurseFromRequest(account, request.getPersonal(), request.getShift());
                    insertNewNurse(account, nur);
                    break;
                }
                case "RECEPTIONIST" : {
                    Receptionist recep = getNewReceptionistFromRequest(account, request.getPersonal());
                    insertNewReceptionist(account, recep);
                    break;
                }
                case "PHARMACIST" : {
                    Pharma pharma = getNewPharmaFromRequest(account, request.getPersonal());
                    insertNewPharma(account, pharma);
                    break;
                }
                default:
                    throw new Exception("Undefined role passed in the request.");
            }

            //Send an email having their respective profile password
            emailService.sendEmailWithPassword(account.getUsername(), account.getPassword());

            resp.setResponse("SUCCESS");
            log.info("User added successfully.");
        } catch (Exception e){
            log.error("Exception occurred while adding new user: "+e.getMessage());
            resp.setError(e.getMessage());
            resp.setResponse("FAILED");
        }
        return resp;
    }

    @Transactional
    void insertNewDoctor(Login account, Doctor doc) {
        loginRepo.save(account);
        doctorRepo.save(doc);
    }

    @Transactional
    void insertNewNurse(Login account, Nurse nur) {
        loginRepo.save(account);
        nurseRepo.save(nur);
    }

    @Transactional
    void insertNewReceptionist(Login account, Receptionist recep) {
        loginRepo.save(account);
        receptionRepo.save(recep);
    }

    @Transactional
    void insertNewPharma(Login account, Pharma pharma) {
        loginRepo.save(account);
        pharmaRepo.save(pharma);
    }

    private Pharma getNewPharmaFromRequest(Login account, PersonalDetail request) {
        Pharma p = new Pharma();
        p.setId(account.getUserId());
        p.setFirstName(request.getFirstName());
        p.setLastName(request.getLastName());
        p.setGender(request.getGender());
        //Need to be added in FIGMA and FrontEnd
        //n.setBirthDate(Date.valueOf(request.getBirthDate()));
        p.setPhoneNumber(request.getPhone());
        p.setBloodGroup(request.getBlood());
        p.setAddress(request.getAddress());
        p.setProfileImage(request.getProfileImage());
        p.setUpdatedAt(OffsetDateTime.now());
        return p;
    }

    private Receptionist getNewReceptionistFromRequest(Login account, PersonalDetail request) {
        Receptionist r = new Receptionist();
        r.setId(account.getUserId());
        r.setFirstName(request.getFirstName());
        r.setLastName(request.getLastName());
        r.setGender(request.getGender());
        //Need to be added in FIGMA and FrontEnd
        //n.setBirthDate(Date.valueOf(request.getBirthDate()));
        r.setPhoneNumber(request.getPhone());
        r.setBloodGroup(request.getBlood());
        r.setAddress(request.getAddress());
        r.setProfileImage(request.getProfileImage());
        r.setUpdatedAt(OffsetDateTime.now());
        return r;
    }

    private Nurse getNewNurseFromRequest(Login account, PersonalDetail request, Shift shift) {
        Nurse n = new Nurse();
        n.setId(account.getUserId());
        n.setFirstName(request.getFirstName());
        n.setLastName(request.getLastName());
        n.setEmail(request.getEmail());
        n.setGender(request.getGender());
        //Need to be added in FIGMA and FrontEnd
        //n.setBirthDate(Date.valueOf(request.getBirthDate()));
        n.setPhoneNumber(request.getPhone());
        n.setBloodGroup(request.getBlood());
        //Need to be added in FIGMA and FrontEnd for doctor and nurse
        //n.setDepartment(request.getDepartment());
        n.setExperience(request.getExperience());
        n.setAddress(request.getAddress());
        n.setProfileImage(request.getProfileImage());
        n.setUpdatedAt(OffsetDateTime.now());
        /*
        n.setMon(shift.getMon());
        n.setTue(shift.getTue());
        n.setWed(shift.getWed());
        n.setThu(shift.getThu());
        n.setFri(shift.getFri());
        n.setSat(shift.getSat());
        n.setSun(shift.getSun());
        */return n;
    }

    private Doctor getNewDoctorFromRequest(Login account, PersonalDetail request, Shift shift) {
        Doctor doc = new Doctor();
        doc.setId(account.getUserId());
        doc.setFirstName(request.getFirstName());
        doc.setLastName(request.getLastName());
        doc.setEmail(request.getEmail());
        doc.setGender(request.getGender());
        //Need to be added in FIGMA and FrontEnd
        //doc.setBirthDate(Date.valueOf(request.getBirthDate()));
        doc.setPhoneNumber(request.getPhone());
        doc.setBloodGroup(request.getBlood());
        //Need to be added in FIGMA and FrontEnd for doctor and nurse
        //doc.setDepartment(request.getDepartment());
        doc.setExperience(request.getExperience());
        doc.setAddress(request.getAddress());
        doc.setProfileImage(request.getProfileImage());
        doc.setUpdatedAt(OffsetDateTime.now());/*
        doc.setMon(shift.getMon());
        doc.setTue(shift.getTue());
        doc.setWed(shift.getWed());
        doc.setThu(shift.getThu());
        doc.setFri(shift.getFri());
        doc.setSat(shift.getSat());
        doc.setSun(shift.getSun());*/
        return doc;
    }

    private Login getNewAccountFromRequest(PersonalDetail personal) {
        Login l = new Login();
        l.setUsername(personal.getEmail());
        l.setActive(true);
        l.setPassword(Utility.generateRandomPassword(8));
        //Generate specific userId for them
        l.setUserId(Utility.getUniqueId());
        l.setRole(personal.getRole());
        l.setUpdatedAt(OffsetDateTime.now());
        return l;
    }

    private void validateNewUserRequest(NewUserRequest request) {
        //Checked by @Valid
    }

    public ViewUserResponse getUsers(String role) {
        ViewUserResponse resp = new ViewUserResponse();
        try{
            if(role == null || role.isEmpty()){
                //Fetch all the active-role users
            }else{
                //Get specific active-role users

            }
        } catch (Exception e){
            log.error("Exception occurred while fetching users by admin : "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }
}