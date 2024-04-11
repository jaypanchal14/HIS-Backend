package org.his.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.config.Roles;
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
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.*;

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

    //Added for transactional methods (which needs to be performed under same session
    @Autowired
    private TransactionalService transService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    FilesStorageService fileService;

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

    public GeneralResp addNewUser(String payload, MultipartFile file) {
        GeneralResp resp = new GeneralResp();
        NewUserRequest request = null;
        String profileImage = null;
        try {

            if(file == null || file.isEmpty() || !Utility.isImage(Path.of(file.getOriginalFilename()))){
                throw new Exception("Pass valid profile-image");
            }
            request = objectMapper.readValue(payload, NewUserRequest.class);

            //Validate request, by checking mandatory field value
            validateNewUserRequest(request);

            //Check if user is already inserted with same email-id
            Optional<Login> optionalAcc = loginRepo.findById(request.getEmail());
            if(optionalAcc.isPresent()){
                throw new Exception("Email-id is already being used, please use another email-id.");
            }

            //Profile-image handling
            String extension = Utility.getFileExtension(file);
            profileImage = Utility.generateNewImageName(extension);
            request.setProfileImage(profileImage);

            //Prepare beans for respective user
            Login account = getNewAccountFromRequest(request);
            //Add entry in login table
            //Add entry in specific user-table
            switch (request.getRole()){
                case "DOCTOR" : {
                    Doctor doc = getNewDoctorFromRequest(account, request);
                    transService.insertNewDoctor(account, doc);
                    break;
                }
                case "NURSE" : {
                    Nurse nur = getNewNurseFromRequest(account, request);
                    transService.insertNewNurse(account, nur);
                    break;
                }
                case "RECEPTIONIST" : {
                    Receptionist recep = getNewReceptionistFromRequest(account, request);
                    transService.insertNewReceptionist(account, recep);
                    break;
                }
                case "PHARMACIST" : {
                    Pharma pharma = getNewPharmaFromRequest(account, request);
                    transService.insertNewPharma(account, pharma);
                    break;
                }
                default:
                    throw new Exception("Undefined role passed in the request.");
            }

            fileService.saveImage(file, profileImage);
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

    private Pharma getNewPharmaFromRequest(Login account, NewUserRequest request) {
        Pharma p = new Pharma();
        p.setId(account.getUserId());
        p.setFirstName(request.getFirstName());
        p.setLastName(request.getLastName());
        p.setGender(request.getGender());
        p.setBirthDate(Date.valueOf(request.getBirthDate()));
        p.setPhoneNumber(request.getPhone());
        p.setBloodGroup(request.getBlood());
        p.setAddress(request.getAddress());
        p.setProfileImage(request.getProfileImage());
        p.setUpdatedAt(OffsetDateTime.now());
        return p;
    }

    private Receptionist getNewReceptionistFromRequest(Login account, NewUserRequest request) {
        Receptionist r = new Receptionist();
        r.setId(account.getUserId());
        r.setFirstName(request.getFirstName());
        r.setLastName(request.getLastName());
        r.setGender(request.getGender());
        r.setBirthDate(Date.valueOf(request.getBirthDate()));
        r.setPhoneNumber(request.getPhone());
        r.setBloodGroup(request.getBlood());
        r.setAddress(request.getAddress());
        r.setProfileImage(request.getProfileImage());
        r.setUpdatedAt(OffsetDateTime.now());
        return r;
    }

    private Nurse getNewNurseFromRequest(Login account, NewUserRequest request) {
        Nurse n = new Nurse();
        n.setId(account.getUserId());
        n.setFirstName(request.getFirstName());
        n.setLastName(request.getLastName());
        n.setGender(request.getGender());
        n.setBirthDate(Date.valueOf(request.getBirthDate()));
        n.setPhoneNumber(request.getPhone());
        n.setBloodGroup(request.getBlood());
        //Need to be added in FIGMA and FrontEnd for doctor and nurse
        //n.setDepartment(request.getDepartment());
        n.setExperience(request.getExperience());
        n.setAddress(request.getAddress());
        n.setProfileImage(request.getProfileImage());
        n.setUpdatedAt(OffsetDateTime.now());
        return n;
    }

    private Doctor getNewDoctorFromRequest(Login account, NewUserRequest request) {
        Doctor doc = new Doctor();
        doc.setId(account.getUserId());
        doc.setFirstName(request.getFirstName());
        doc.setLastName(request.getLastName());
        doc.setGender(request.getGender());
        doc.setBirthDate(Date.valueOf(request.getBirthDate()));
        doc.setPhoneNumber(request.getPhone());
        doc.setBloodGroup(request.getBlood());
        //Need to be added in FIGMA and FrontEnd for doctor and nurse
        //doc.setDepartment(request.getDepartment());
        doc.setExperience(request.getExperience());
        doc.setAddress(request.getAddress());
        doc.setProfileImage(request.getProfileImage());
        doc.setUpdatedAt(OffsetDateTime.now());
        return doc;
    }

    private Login getNewAccountFromRequest(NewUserRequest personal) {
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

        //In case they sent it in lower
        request.setRole(request.getRole().toUpperCase());
    }

    public ViewUserResponse getUsers(String role) {
        ViewUserResponse resp = new ViewUserResponse();
        List<PersonalDetail> ls = null;
        List<ViewUserIdentifier> identifiers;
        RoleBasedMapping mapping = null;
        try{
            if(role == null || role.isEmpty()){
                //Fetch all the active-role users
                identifiers = loginRepo.getActiveUsers();

            }else{
                //Check if valid role
                if(!Roles.isValidRole(role)){
                    throw new Exception("Please pass valid ROLE in the request-param");
                }
                role = role.toUpperCase();
                //Get specific active-role users
                identifiers = loginRepo.getActiveUsersBasedOnRole(role);
            }
            mapping = getRoleBasedMappingsFromIdentifiers(identifiers);
            getUsersDetailsFromMapping(mapping, resp);
            log.info("Request processed successfully for fetching active users.");
        } catch (Exception e){
            log.error("Exception occurred while fetching users by admin : "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }

    private void getUsersDetailsFromMapping(RoleBasedMapping mapping, ViewUserResponse resp) {
        List<PersonalDetail> doctor = new ArrayList<>();
        List<PersonalDetail> nurse = new ArrayList<>();
        List<PersonalDetail> pharmacist = new ArrayList<>();
        List<PersonalDetail> receptionist = new ArrayList<>();
        List<String> userIds;
        List<Doctor> docIds;
        List<Nurse> nurseIds;
        List<Pharma> pharmaIds;
        List<Receptionist> receptionIds;
        if(!mapping.getDoctors().isEmpty()){
            userIds = mapping.getDoctors().keySet().stream().toList();
            docIds = doctorRepo.findAllByIdIn(userIds);
            for(Doctor doc : docIds){
                PersonalDetail detail = mapping.getDoctors().get(doc.getId());
                detail.setFirstName(doc.getFirstName());
                detail.setLastName(doc.getLastName());
                detail.setPhone(doc.getPhoneNumber());
                detail.setGender(doc.getGender());
                detail.setBlood(doc.getBloodGroup());
                detail.setAddress(doc.getAddress());
                detail.setActive(true);
                detail.setBirthDate(doc.getBirthDate().toString());
                doctor.add(detail);
            }
        }

        if(!mapping.getNurses().isEmpty()){
            userIds = mapping.getNurses().keySet().stream().toList();
            nurseIds = nurseRepo.findAllByIdIn(userIds);
            for(Nurse n : nurseIds){
                PersonalDetail detail = mapping.getNurses().get(n.getId());
                detail.setFirstName(n.getFirstName());
                detail.setLastName(n.getLastName());
                detail.setPhone(n.getPhoneNumber());
                detail.setGender(n.getGender());
                detail.setBlood(n.getBloodGroup());
                detail.setAddress(n.getAddress());
                detail.setActive(true);
                detail.setBirthDate(n.getBirthDate().toString());
                nurse.add(detail);
            }
        }

        if(!mapping.getPharmacists().isEmpty()){
            userIds = mapping.getPharmacists().keySet().stream().toList();
            pharmaIds = pharmaRepo.findAllByIdIn(userIds);
            for(Pharma p : pharmaIds){
                PersonalDetail detail = mapping.getPharmacists().get(p.getId());
                detail.setFirstName(p.getFirstName());
                detail.setLastName(p.getLastName());
                detail.setPhone(p.getPhoneNumber());
                detail.setGender(p.getGender());
                detail.setBlood(p.getBloodGroup());
                detail.setAddress(p.getAddress());
                detail.setActive(true);
                detail.setBirthDate(p.getBirthDate().toString());
                pharmacist.add(detail);
            }
        }

        if(!mapping.getReceptionists().isEmpty()){
            userIds = mapping.getReceptionists().keySet().stream().toList();
            receptionIds = receptionRepo.findAllByIdIn(userIds);
            for(Receptionist r : receptionIds){
                PersonalDetail detail = mapping.getReceptionists().get(r.getId());
                detail.setFirstName(r.getFirstName());
                detail.setLastName(r.getLastName());
                detail.setPhone(r.getPhoneNumber());
                detail.setGender(r.getGender());
                detail.setBlood(r.getBloodGroup());
                detail.setAddress(r.getAddress());
                detail.setActive(true);
                detail.setBirthDate(r.getBirthDate().toString());
                receptionist.add(detail);
            }
        }
        resp.setDoctor(doctor);
        resp.setNurse(nurse);
        resp.setPharmacist(pharmacist);
        resp.setReceptionist(receptionist);
    }

    private RoleBasedMapping getRoleBasedMappingsFromIdentifiers(List<ViewUserIdentifier> identifiers) {
        RoleBasedMapping mapping = new RoleBasedMapping();
        Map<String, PersonalDetail> doctors = new HashMap<>();
        Map<String, PersonalDetail> nurses = new HashMap<>();
        Map<String, PersonalDetail> pharmacists = new HashMap<>();
        Map<String, PersonalDetail> receptionists = new HashMap<>();

        for(ViewUserIdentifier view : identifiers){
            PersonalDetail detail = new PersonalDetail();
            detail.setUserId(view.getUserId());
            detail.setEmail(view.getEmail());
            if("DOCTOR".equals(view.getRole())){
                detail.setRole("DOCTOR");
                doctors.put(view.getUserId(),detail);
            } else if ("NURSE".equals(view.getRole())) {
                detail.setRole("NURSE");
                nurses.put(view.getUserId(),detail);
            } else if ("RECEPTIONIST".equals(view.getRole())) {
                detail.setRole("RECEPTIONIST");
                receptionists.put(view.getUserId(),detail);
            } else if ("PHARMACIST".equals(view.getRole())) {
                detail.setRole("PHARMACIST");
                pharmacists.put(view.getUserId(),detail);
            } else {

            }
        }
        mapping.setDoctors(doctors);
        mapping.setNurses(nurses);
        mapping.setPharmacists(pharmacists);
        mapping.setReceptionists(receptionists);
        return mapping;
    }

    public String tryImage() {
        return fileService.loadImage("ff237a5b4fd14871712229626350.png");
    }
}