package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.config.Roles;
import org.his.entity.Admit;
import org.his.entity.user.*;
import org.his.exception.AuthenticationException;
import org.his.repo.AdmitRepo;
import org.his.repo.LoginRepo;
import org.his.repo.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private PatientRepo patientRepo;

    @Autowired
    private AdmitRepo admitRepo;

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
        obj.setProfileImage(receptionist.getProfileImage());
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
        obj.setProfileImage(pharma.getProfileImage());
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
        obj.setProfileImage(doctor.getProfileImage());
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
        obj.setProfileImage(nurse.getProfileImage());
        return obj;
    }

    private PersonalDetail getDetailForAdmin(Admin admin) {
        PersonalDetail obj = new PersonalDetail();
        obj.setRole("ADMIN");
        obj.setFirstName(admin.getFirstName());
        obj.setLastName(admin.getLastName());
        obj.setAddress(admin.getAddress());
        obj.setBirthDate(admin.getBirthDate().toString());
        obj.setBlood(admin.getBloodGroup());
        obj.setGender(admin.getGender());
        obj.setPhone(admin.getPhoneNumber());
        obj.setProfileImage(admin.getProfileImage());
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

    public PersonalDetailResp updateProfile(String id, String role, PersonalDetail profileData) {
        PersonalDetailResp response = new PersonalDetailResp();

        switch (role) {
            case "DOCTOR":
                Optional<Doctor> doctorOptional = doctorRepo.findById(id);
                if (doctorOptional.isPresent()) {
                    Doctor doctor = doctorRepo.save(getDoctor(profileData, doctorOptional));
                    response.setResponse(getDetailForDoc(doctor));
                } else {
                    response.setError("Doctor not found.");
                }
                break;

            case "ADMIN":
                Optional<Admin> adminOptional = adminRepo.findById(id);
                if (adminOptional.isPresent()) {
                    Admin admin = adminRepo.save(getAdmin(profileData, adminOptional));
                    response.setResponse(getDetailForAdmin(admin));
                } else {
                    response.setError("Admin not found.");
                }
                break;

            case "NURSE":
                Optional<Nurse> nurseOptional = nurseRepo.findById(id);
                if (nurseOptional.isPresent()) {
                    Nurse nurse = nurseRepo.save(getNurse(profileData, nurseOptional));
                    response.setResponse(getDetailForNurse(nurse));
                } else {
                    response.setError("Nurse not found.");
                }
                break;

            case "PHARMACIST":
                Optional<Pharma> pharmaOptional = pharmaRepo.findById(id);
                if (pharmaOptional.isPresent()) {
                    Pharma pharma = pharmaRepo.save(getPharmacist(profileData, pharmaOptional));
                    response.setResponse(getDetailForPharma(pharma));
                } else {
                    response.setError("Pharma not found.");
                }
                break;

            case "Receptionist":
                Optional<Receptionist> receptionistOptional = receptionRepo.findById(id);
                if (receptionistOptional.isPresent()) {
                    Receptionist receptionist = receptionRepo.save(getReceptionist(profileData, receptionistOptional));
                    response.setResponse(getDetailForReceptionist(receptionist));
                } else {
                    response.setError("Receptionist not found.");
                }
                break;

            default:
                response.setError("Role not supported.");
        }

        return response;
    }

    private static Doctor getDoctor(PersonalDetail profileData, Optional<Doctor> doctorOptional) {
        Doctor doctor = doctorOptional.get();
        if (profileData.getFirstName() != null) {
            doctor.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null) {
            doctor.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null) {
            doctor.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getDepartment() != null) {
            doctor.setDepartment(profileData.getDepartment());
        }
        if (profileData.getSpecialization() != null) {
            doctor.setSpecialization(profileData.getSpecialization());
        }
        if (profileData.getAddress() != null) {
            doctor.setAddress(profileData.getAddress());
        }
        // Set imagePath if needed
        if (profileData.getProfileImage() != null) {
            doctor.setProfileImage(profileData.getProfileImage());
        }
        return doctor;
    }

    private static Admin getAdmin(PersonalDetail profileData, Optional<Admin> adminOptional) {
        Admin admin = adminOptional.get();
        if (profileData.getFirstName() != null) {
            admin.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null) {
            admin.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null) {
            admin.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null) {
            admin.setAddress(profileData.getAddress());
        }
        // Set imagePath if needed
        if (profileData.getProfileImage() != null) {
            admin.setProfileImage(profileData.getProfileImage());
        }
        return admin;
    }

    private static Nurse getNurse(PersonalDetail profileData, Optional<Nurse> nurseOptional) {
        Nurse nurse = nurseOptional.get();
        if (profileData.getFirstName() != null) {
            nurse.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null) {
            nurse.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null) {
            nurse.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null) {
            nurse.setAddress(profileData.getAddress());
        }
        if (profileData.getProfileImage() != null) {
            nurse.setProfileImage(profileData.getProfileImage());
        }
        return nurse;
    }

    private static Pharma getPharmacist(PersonalDetail profileData, Optional<Pharma> pharmaOptional) {
        Pharma pharma = pharmaOptional.get();
        if (profileData.getFirstName() != null) {
            pharma.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null) {
            pharma.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null) {
            pharma.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null) {
            pharma.setAddress(profileData.getAddress());
        }
        // Set imagePath if needed
        if (profileData.getProfileImage() != null) {
            pharma.setProfileImage(profileData.getProfileImage());
        }
        return pharma;
    }

    private static Receptionist getReceptionist(PersonalDetail profileData, Optional<Receptionist> receptionistOptional) {
        Receptionist receptionist = receptionistOptional.get();
        if (profileData.getFirstName() != null) {
            receptionist.setFirstName(profileData.getFirstName());
        }
        if (profileData.getLastName() != null) {
            receptionist.setLastName(profileData.getLastName());
        }
        if (profileData.getPhone() != null) {
            receptionist.setPhoneNumber(profileData.getPhone());
        }
        if (profileData.getAddress() != null) {
            receptionist.setAddress(profileData.getAddress());
        }
        // Set imagePath if needed
        if (profileData.getProfileImage() != null) {
            receptionist.setProfileImage(profileData.getProfileImage());
        }
        return receptionist;
    }

    private boolean isValidNurse(String nurseId) {
        Nurse nurse = nurseRepo.findById(nurseId).orElse(null);
        if(nurse == null) {
            return false;  // Nurse ID not found
        }
        return true;
    }
    private boolean isValidDoctor(String doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null) {
            return false;  // Nurse ID not found
        }
        return true;
    }
    public PatientResponse viewLivePatients(String role, String id, int isOP) {
        PatientResponse response = new PatientResponse();

        // Check if the role is supported
        if (!role.equalsIgnoreCase("DOCTOR") && !role.equalsIgnoreCase("NURSE")) {
            response.setError("Role not supported.");
            return response;
        }

        if(role.equalsIgnoreCase("NURSE"))
        {
            if (!isValidNurse(id)) {
                response.setError("Invalid nurse credentials or unauthorized access.");
                return response;
            }
        }
        else {
            if (!isValidDoctor(id)) {
                response.setError("Invalid Doctor credentials or unauthorized access.");
                return response;
            }
        }
        List<PatientDetail> livePatients = new ArrayList<>();

        // Fetch all admits from the database
        List<Admit> admits = admitRepo.findAll();

        // Iterate through admits and filter live patients based on role and isOP flag
        for (Admit admit : admits) {
            // Check if the patient is live (isActive) and matches the isOP flag
            if (admit.isActive() && (isOP == 1 && "OP".equalsIgnoreCase(admit.getPatientType()) || isOP == 0 && "IP".equalsIgnoreCase(admit.getPatientType()))) {
                PatientDetail patientDetail = getPatientDetailById(admit);
                if (patientDetail != null) {
                    // Add patient details to the list of live patients
                    livePatients.add(patientDetail);
                }
            }
        }

        // Set the response
        response.setResponse(livePatients);
        return response;
    }

    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    // Fetches patient details from Admit and Patient entities and constructs a PatientDetail object
    private PatientDetail getPatientDetailById(Admit admit) {
        Optional<Patient> patientOptional = patientRepo.findById(admit.getPatientId());
        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            PatientDetail patientDetail = new PatientDetail();
            patientDetail.setId(patient.getId());
            patientDetail.setAdmitId(admit.getAdmitId());
            patientDetail.setFirstName(patient.getFirstName());
            patientDetail.setLastName(patient.getLastName());
            patientDetail.setPhone(patient.getPhoneNumber());
            patientDetail.setGender(patient.getGender());
            patientDetail.setBlood(patient.getBloodGroup());
            patientDetail.setWardNo(patient.getWardNo());
            patientDetail.setAddress(patient.getAddress());
            patientDetail.setBirthDate(dateFormat.format(patient.getBirthDate()));
//            patientDetail.set(admit.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS")));
            return patientDetail;
        }
        return null;
    }


    public PatientResponse viewOneLivePatient(String role, String id, String patientId) {
        PatientResponse response = new PatientResponse();

        if(role.equalsIgnoreCase("NURSE"))
        {
            if (!isValidNurse(id)) {
                response.setError("Invalid nurse credentials or unauthorized access.");
                return response;
            }
        }
        else {
            if (!isValidDoctor(id)) {
                response.setError("Invalid Doctor credentials or unauthorized access.");
                return response;
            }
        }

        if (!role.equalsIgnoreCase("DOCTOR") && !role.equalsIgnoreCase("NURSE")) {
            response.setError("Role not supported.");
            return response;
        }

        List<PatientDetail> livePatients = new ArrayList<>();

        Optional<Admit> admitOptional = admitRepo.findByPatientId(patientId);
        if (admitOptional.isPresent()) {
            Admit admit = admitOptional.get();
            // Check if the patient is live (isActive)
            if (admit.isActive()) {
                // Fetch patient details based on admit's patientId
                PatientDetail patientDetail = getPatientDetailsById(admit);
                if (patientDetail != null) {
                    // Set the response
                    livePatients.add(patientDetail);
                    response.setResponse(livePatients);
                    return response;
                }
            } else {
                response.setError("Patient is not live.");
                return response;
            }
        }

        response.setError("Patient not found or not live.");
        return response;
    }

    // Fetches patient details from Admit and Patient entities and constructs a PatientDetail object
    private PatientDetail getPatientDetailsById(Admit admit) {
        Optional<Patient> patientOptional = patientRepo.findById(admit.getPatientId());
        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            PatientDetail patientDetail = new PatientDetail();
            patientDetail.setId(patient.getId());
            patientDetail.setAdmitId(admit.getAdmitId());
            patientDetail.setFirstName(patient.getFirstName());
            patientDetail.setLastName(patient.getLastName());
            patientDetail.setPhone(patient.getPhoneNumber());
            patientDetail.setGender(patient.getGender());
            patientDetail.setBlood(patient.getBloodGroup());
            patientDetail.setWardNo(patient.getWardNo());
            patientDetail.setAddress(patient.getAddress());
            patientDetail.setBirthDate(patient.getBirthDate().toString());
            return patientDetail;
        }
        return null;
    }

}