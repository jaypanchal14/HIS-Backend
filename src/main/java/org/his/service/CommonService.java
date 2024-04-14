package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.entity.user.*;
import org.his.exception.AuthenticationException;
import org.his.repo.LoginRepo;
import org.his.repo.user.*;
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
    private PharmaRepo pharmaRepo;

    @Autowired
    private ReceptionistRepo receptionRepo;

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private FilesStorageService fileService;

    public PersonalDetailResp getPersonalDetail(String id, String role){
        PersonalDetailResp resp = new PersonalDetailResp();

        switch(role) {
            case "ADMIN":
                Optional<Admin> optAdmin = adminRepo.findById(id);
                if (optAdmin.isEmpty()) {
                    resp.setError("Username not found");
                }else{
                    PersonalDetail detail = getDetailForAdmin(optAdmin.get());
                    resp.setResponse(detail);
                }
                break;

            case "NURSE":
                Optional<Nurse> optNurse = nurseRepo.findById(id);
                if (optNurse.isEmpty()) {
                    resp.setError("Username not found");
                }else{
                    PersonalDetail detail = getDetailForNurse(optNurse.get());
                    resp.setResponse(detail);
                }
                break;

            case "DOCTOR":
                Optional<Doctor> optDoc = doctorRepo.findById(id);
                if (optDoc.isEmpty()) {
                    resp.setError("Username not found");
                }else{
                    PersonalDetail detail = getDetailForDoc(optDoc.get());
                    resp.setResponse(detail);
                }
                break;

            case "PHARMACIST":
                Optional<Pharma> optPharma = pharmaRepo.findById(id);
                if (optPharma.isEmpty()) {
                    resp.setError("Username not found");
                }else{
                    PersonalDetail detail = getDetailForPharma(optPharma.get());
                    resp.setResponse(detail);
                }
                break;

            case "RECEPTIONIST":
                Optional<Receptionist> optReception = receptionRepo.findById(id);
                if (optReception.isEmpty()) {
                    resp.setError("Username not found");
                }else{
                    PersonalDetail detail = getDetailForReceptionist(optReception.get());
                    resp.setResponse(detail);
                }
                break;
            default:
                log.error("Undefined role passed in the request.");
                resp.setError("Undefined role passed in the request");
        }

        return resp;
    }

    private PersonalDetail getDetailForReceptionist(Receptionist receptionist) {
        PersonalDetail obj = new PersonalDetail();
        obj.setRole("RECEPTIONIST");
        obj.setFirstName(receptionist.getFirstName());
        obj.setLastName(receptionist.getLastName());
        obj.setAddress(receptionist.getAddress());
        obj.setBirthDate(receptionist.getBirthDate().toString());
        obj.setBlood(receptionist.getBloodGroup());
        obj.setGender(receptionist.getGender());
        obj.setPhone(receptionist.getPhoneNumber());
        obj.setProfileImage(fileService.loadUserImage(receptionist.getProfileImage()));
        return obj;
    }

    private PersonalDetail getDetailForPharma(Pharma pharma) {
        PersonalDetail obj = new PersonalDetail();
        obj.setRole("PHARMACIST");
        obj.setFirstName(pharma.getFirstName());
        obj.setLastName(pharma.getLastName());
        obj.setAddress(pharma.getAddress());
        obj.setBirthDate(pharma.getBirthDate().toString());
        obj.setBlood(pharma.getBloodGroup());
        obj.setGender(pharma.getGender());
        obj.setPhone(pharma.getPhoneNumber());
        obj.setProfileImage(fileService.loadUserImage(pharma.getProfileImage()));
        return obj;
    }

    private PersonalDetail getDetailForDoc(Doctor doctor) {
        PersonalDetail obj = new PersonalDetail();
        obj.setRole("DOCTOR");
        obj.setFirstName(doctor.getFirstName());
        obj.setLastName(doctor.getLastName());
        obj.setAddress(doctor.getAddress());
        obj.setBirthDate(doctor.getBirthDate().toString());
        obj.setBlood(doctor.getBloodGroup());
        obj.setGender(doctor.getGender());
        obj.setDepartment(doctor.getDepartment());
        obj.setExperience(doctor.getExperience());
        obj.setSpecialization(doctor.getSpecialization());
        obj.setPhone(doctor.getPhoneNumber());
        obj.setProfileImage(fileService.loadUserImage(doctor.getProfileImage()));
        return obj;
    }

    private PersonalDetail getDetailForNurse(Nurse nurse) {
        PersonalDetail obj = new PersonalDetail();
        obj.setRole("NURSE");
        obj.setFirstName(nurse.getFirstName());
        obj.setLastName(nurse.getLastName());
        obj.setAddress(nurse.getAddress());
        obj.setBirthDate(nurse.getBirthDate().toString());
        obj.setBlood(nurse.getBloodGroup());
        obj.setGender(nurse.getGender());
        obj.setHead(nurse.isHead());
        obj.setSpecialization(nurse.getSpecialization());
        obj.setPhone(nurse.getPhoneNumber());
        obj.setProfileImage(fileService.loadUserImage(nurse.getProfileImage()));
        return obj;
    }

    private PersonalDetail getDetailForAdmin(Admin admin) {
        PersonalDetail obj = new PersonalDetail();
        obj.setRole("ADMIN");
        obj.setFirstName(admin.getFirstName());
        obj.setLastName(admin.getLastName());
        obj.setGender(admin.getGender());
        obj.setAddress(admin.getAddress());
        obj.setBirthDate(admin.getBirthDate().toString());
        obj.setBlood(admin.getBloodGroup());
        obj.setPhone(admin.getPhoneNumber());
        obj.setProfileImage(fileService.loadUserImage(admin.getProfileImage()));
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
            role = role.toUpperCase();
            if(!"DOCTOR".equals(role) && !"NURSE".equals(role)){
                msg = "Pass correct ROLE of the user.";
                throw new Exception("Role other than doctor or nurse has been passed in request.");
            }
            Optional<String> optUserId = loginRepo.findUserIdByUsername(email, role);
            if (optUserId.isEmpty()) {
                msg = "Email not found in the database.";
                throw new AuthenticationException(msg);
            }

            String userId = optUserId.get();
            ScheduleDetail detail = null;
            if("DOCTOR".equals(role)){
                Optional<Doctor> doc = doctorRepo.findById(userId);
                if(doc.isEmpty()){
                    msg = "Doctor not found in the doctor table";
                    throw new Exception(msg);
                }
                detail = getScheduleDetailForDoc(doc.get(), email);
            } else {
                Optional<Nurse> nurse = nurseRepo.findById(userId);
                if(nurse.isEmpty()){
                    msg = "Nurse not found in the nurse table";
                    throw new Exception(msg);
                }
                detail = getScheduleDetailForNurse(nurse.get(), email);
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

    public PersonalDetailResp updateProfile(PersonalDetail request) {
        PersonalDetailResp response = new PersonalDetailResp();
        try {
            if(request.getRole() == null || request.getRole().isEmpty()){
                throw new RuntimeException("Empty role passed in the request");
            }
            String role = request.getRole().toUpperCase();
            request.setRole(role);
            switch (role) {
                case "ADMIN": {
                    Optional<Admin> adminOptional = adminRepo.findById(request.getUserId());
                    if (adminOptional.isPresent()) {
                        Admin admin = adminRepo.save(getAdmin(request, adminOptional.get()));
                        response.setResponse(getDetailForAdmin(admin));
                    } else {
                        response.setError("Admin not found.");
                    }
                    break;
                }
                case "DOCTOR": {
                    Optional<Doctor> doctorOptional = doctorRepo.findById(request.getUserId());
                    if (doctorOptional.isPresent()) {
                        Doctor doctor = doctorRepo.save(getDoctor(request, doctorOptional.get()));
                        response.setResponse(getDetailForDoc(doctor));
                    } else {
                        response.setError("Doctor not found.");
                    }
                    break;
                }
                case "NURSE": {
                    Optional<Nurse> nurseOptional = nurseRepo.findById(request.getUserId());
                    if (nurseOptional.isPresent()) {
                        Nurse nurse = nurseRepo.save(getNurse(request, nurseOptional.get()));
                        response.setResponse(getDetailForNurse(nurse));
                    } else {
                        response.setError("Nurse not found.");
                    }
                    break;
                }
                case "PHARMACIST": {
                    Optional<Pharma> pharmaOptional = pharmaRepo.findById(request.getUserId());
                    if (pharmaOptional.isPresent()) {
                        Pharma pharma = pharmaRepo.save(getPharmacist(request, pharmaOptional.get()));
                        response.setResponse(getDetailForPharma(pharma));
                    } else {
                        response.setError("Pharma not found.");
                    }
                    break;
                }
                case "RECEPTIONIST": {
                    Optional<Receptionist> receptionistOptional = receptionRepo.findById(request.getUserId());
                    if (receptionistOptional.isPresent()) {
                        Receptionist receptionist = receptionRepo.save(getReceptionist(request, receptionistOptional.get()));
                        response.setResponse(getDetailForReceptionist(receptionist));
                    } else {
                        response.setError("Receptionist not found.");
                    }
                    break;
                }
                default:
                    response.setError("Role not supported.");
                    throw new RuntimeException("Invalid role passed in the request");
            }
        }catch (Exception e){
            log.error("updateProfile : Exception occurred while updating: "+e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    private Doctor getDoctor(PersonalDetail profileData, Doctor doctor) {
        if (profileData.getFirstName() != null && !profileData.getFirstName().isBlank()) {
            doctor.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null && !profileData.getLastName().isBlank()) {
            doctor.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null && !profileData.getPhone().isBlank()) {
            doctor.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getDepartment() != null && !profileData.getDepartment().isBlank()) {
            doctor.setDepartment(profileData.getDepartment());
        }
        if (profileData.getSpecialization() != null && !profileData.getSpecialization().isBlank()) {
            doctor.setSpecialization(profileData.getSpecialization());
        }
        if (profileData.getAddress() != null && !profileData.getAddress().isBlank()) {
            doctor.setAddress(profileData.getAddress());
        }
        return doctor;
    }

    private Admin getAdmin(PersonalDetail profileData, Admin admin) {
        if (profileData.getFirstName() != null && !profileData.getFirstName().isBlank()) {
            admin.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null && !profileData.getLastName().isBlank()) {
            admin.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null && !profileData.getPhone().isBlank()) {
            admin.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null && !profileData.getAddress().isBlank()) {
            admin.setAddress(profileData.getAddress());
        }
        return admin;
    }

    private Nurse getNurse(PersonalDetail profileData, Nurse nurse) {
        if (profileData.getFirstName() != null && !profileData.getFirstName().isBlank()) {
            nurse.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null && !profileData.getLastName().isBlank()) {
            nurse.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null && !profileData.getPhone().isBlank()) {
            nurse.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null && !profileData.getAddress().isBlank()) {
            nurse.setAddress(profileData.getAddress());
        }
        return nurse;
    }

    private Pharma getPharmacist(PersonalDetail profileData, Pharma pharma) {
        if (profileData.getFirstName() != null && !profileData.getFirstName().isBlank()) {
            pharma.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null && !profileData.getLastName().isBlank()) {
            pharma.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null && !profileData.getPhone().isBlank()) {
            pharma.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null && !profileData.getAddress().isBlank()) {
            pharma.setAddress(profileData.getAddress());
        }
        return pharma;
    }

    private Receptionist getReceptionist(PersonalDetail profileData, Receptionist receptionist) {
        if (profileData.getFirstName() != null && !profileData.getFirstName().isBlank()) {
            receptionist.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null && !profileData.getLastName().isBlank()) {
            receptionist.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null && !profileData.getPhone().isBlank()) {
            receptionist.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null && !profileData.getAddress().isBlank()) {
            receptionist.setAddress(profileData.getAddress());
        }
        return receptionist;
    }

}