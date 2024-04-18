package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.config.Roles;
import org.his.entity.Admit;
import org.his.entity.Login;
import org.his.entity.user.Doctor;
import org.his.entity.user.Patient;
import org.his.entity.user.Receptionist;
import org.his.exception.NoSuchAccountException;
import org.his.exception.RequestValidationException;
import org.his.repo.AdmitRepo;
import org.his.repo.LoginRepo;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.PatientRepo;
import org.his.repo.user.ReceptionistRepo;
import org.his.util.ShiftUtility;
import org.his.util.Utility;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReceptionistService {

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private ReceptionistRepo receptionRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private FilesStorageService fileService;

    @Autowired
    private AdmitRepo admitRepo;

    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

    public ReceptionDetailResp getAvailableDoctor(String userId) {
        ReceptionDetailResp response = new ReceptionDetailResp();
        try {
            List<PersonalDetail> personalDetails = new ArrayList<>();

            List<ViewUserIdentifier> activeDoctor = loginRepo.getActiveUsersBasedOnRole("DOCTOR");

            if (activeDoctor.isEmpty()) {
                response.setResponse(personalDetails);
                return response;
            }

            List<String> doctorIds = new ArrayList<>();
            for (ViewUserIdentifier identifier : activeDoctor) {
                doctorIds.add(identifier.getUserId());
            }

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            //0 : no shift, 1 : 12:00 AM to 08:59 AM, 2 : 09:00 AM to 04:59 PM, 3: 05:00PM to 11:59PM
            //0 : no shift, 1 : 00:00 to 08:59, 2 : 09:00 to 16:59, 3 : 17:00 to 23:59
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            //log.info("Current day:"+currentDayOfWeek+", hour: "+hour);
            List<Doctor> doctors = doctorRepo.findAllByIdIn(doctorIds);

            //Add logic to filter-out only on-shift doctor
            for (Doctor doctor : doctors) {
                if(ShiftUtility.isDoctorOnShift(doctor, hour, currentDayOfWeek)){
                    PersonalDetail detail = new PersonalDetail();
                    detail.setFirstName(doctor.getFirstName());
                    detail.setLastName(doctor.getLastName());
                    detail.setPhone(doctor.getPhoneNumber());
                    detail.setGender(doctor.getGender());
                    detail.setBlood(doctor.getBloodGroup());
                    detail.setAddress(doctor.getAddress());
                    detail.setBirthDate(doctor.getBirthDate().toString());
                    detail.setProfileImage(doctor.getProfileImage());

                    personalDetails.add(detail);
                }
            }
            response.setResponse(personalDetails);
            log.info("getAvailableDoctor | request processed successfully.");
        } catch (Exception e) {
            log.error("getAvailableDoctor | Exception occurred while fetching on-shift doctor: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    public GeneralResp registerPatient(PatientDetail request) {
        GeneralResp response = new GeneralResp();
        String profileImage = null;
        Patient patient = null;
        boolean isFilePresent = false;
        try {

            if (request.getAadhaar() == null || request.getAadhaar().isBlank()) {
                throw new RuntimeException("Empty AADHAAR-NUMBER passed in the request");
            }

            //Check if the patient already have one active admit table entry
            Optional<Admit> existingAdmit = admitRepo.findByPatientIdAndActiveIsTrue(request.getAadhaar());
            if(existingAdmit.isPresent()){
                throw new Exception("Patient already have one active admit table entry");
            }

            //check with aadhar, if patient is already existing
            Optional<Patient> optPatient = patientRepo.findById(request.getAadhaar());

            if (request.getIsNewPatient() == 1) {
                //New patient
                //if patient already exists, throw an exception
                if (optPatient.isPresent()) {
                    throw new Exception("Patient already exists with this AADHAAR-number.");
                }

                validateNewPatientRequest(request);

                if(request.getImage() != null && !request.getImage().isEmpty()){
                    isFilePresent = true;

                    String extension = Utility.getFileExtension(request.getImage());
                    profileImage = Utility.generateNewImageName(extension);
                }

                patient = getNewPatientFromRequest(request);
                patient.setProfileImage(profileImage);
                Admit admit = getNewAdmitObjectForRequest(patient, request);
                patientRepo.save(patient);
                admitRepo.save(admit);

                if(isFilePresent){
                    //Only save image if it's found in the request
                    fileService.savePatientProfile(request.getImage(), profileImage);
                }

            } else {
                //Existing patient

                if (optPatient.isEmpty()) {
                    throw new Exception("Patient doesn't exists with this AADHAAR-number.");
                }
                patient = optPatient.get();
                //Update required fields in patient object
                patient.setPatientType("OP");
                patient.setWardNo("");
                patient.setUpdatedAt(OffsetDateTime.now());
                Admit admit = getNewAdmitObjectForRequest(patient, request);

                patientRepo.save(patient);
                admitRepo.save(admit);
            }

            //Generate success response
            response.setResponse("SUCCESS");
            log.info("registerPatient | Patient registered successfully.");
        } catch (Exception e) {
            log.error("registerPatient | Exception occurred while registering patient: " + e.getMessage());
            response.setResponse("FAILED");
            response.setError(e.getMessage());
        }

        return response;
    }

    private void validateNewPatientRequest(PatientDetail request) throws Exception {
        if (request.getAadhaar() == null || request.getAadhaar().isBlank() ||
                request.getFirstName() == null || request.getFirstName().isBlank() ||
//                request.getLastName() == null || request.getLastName().isBlank() ||
                request.getPhone() == null || request.getPhone().isBlank() ||
                request.getGender() == null || request.getGender().isBlank() ||
                request.getBlood() == null || request.getBlood().isBlank() ||
                request.getBirthDate() == null || request.getBirthDate().isBlank()) {
            throw new Exception("Empty field-value passed in the mandatory fields");
        }

        //As possible that no image passed while registration
//        if (request.getImage() == null || request.getImage().isEmpty() || !Utility.isImage(Path.of(request.getImage().getOriginalFilename()))) {
//            throw new Exception("Please pass valid image");
//        }

    }

    private Admit getNewAdmitObjectForRequest(Patient patient, PatientDetail request) {
        Admit admit = new Admit();
        admit.setAdmitId(Utility.getUniqueId());
        admit.setPatientId(patient.getAadhar());
        admit.setDate(OffsetDateTime.now());
        admit.setPatientType(patient.getPatientType());
        admit.setActive(true);
        admit.setEmergency(false);
        if(request.getRemark() != null && !request.getRemark().isBlank()){
            admit.setRemark(stringEncryptor.encrypt(request.getRemark()));
        }else{
            admit.setRemark(null);
        }
        return admit;
    }

    public PatientResponse isPatientPresent(PatientDetail request) {
        PatientResponse response = new PatientResponse();
        try {
            // Check if all required fields are present
            if ((request.getAadhaar() == null || request.getAadhaar().isBlank()) &&
                    ((request.getFirstName() == null || request.getFirstName().isBlank() ||
                            request.getLastName() == null) || request.getLastName().isBlank())
            ) {
                throw new Exception("Either aadhaar-number or patient's first and last name must be provided.");
            }

            List<PatientDetail> ls = new ArrayList<>();
            if (request.getAadhaar() != null && !request.getAadhaar().isBlank()) {
                Patient patient = patientRepo.findById(request.getAadhaar()).orElse(null);
                if (patient != null) {
                    ls.add(getPatientDetailsFromPatient(patient));
                    response.setResponse(ls);
                } else {
                    response.setResponse(ls);
                }
            } else {
                // Search patient by first and last name
                List<Patient> patients = patientRepo.findByFirstNameAndLastName(request.getFirstName().toLowerCase(), request.getLastName().toLowerCase());
                if (!patients.isEmpty()) {
                    for (Patient p : patients) {
                        ls.add(getPatientDetailsFromPatient(p));
                    }
                    response.setResponse(ls);
                } else {
                    response.setResponse(ls);
                }
            }
        } catch (Exception e) {
            log.error("isPatientPresent | Exception occurred while searching patient: " + e.getMessage());
            response.setError(e.getMessage());
        }

        return response;
    }

    private PatientDetail getPatientDetailsFromPatient(Patient patient) {
        PatientDetail detail = new PatientDetail();
        detail.setAadhaar(patient.getAadhar());
        detail.setFirstName(patient.getFirstName());
        detail.setLastName(patient.getLastName());
        detail.setGender(stringEncryptor.decrypt(patient.getGender()));
        detail.setPhone(stringEncryptor.decrypt(patient.getPhoneNumber()));
        if(patient.getAddress() != null && !patient.getAddress().isBlank()){
            detail.setAddress(stringEncryptor.decrypt(patient.getAddress()));
        }else{
            detail.setAddress("");
        }
        return detail;
    }

    private Patient getNewPatientFromRequest(PatientDetail request) {
        Patient newPatient = new Patient();
        newPatient.setAadhar(request.getAadhaar());
        newPatient.setFirstName(request.getFirstName().toLowerCase());
        newPatient.setLastName(request.getLastName().toLowerCase());
        if(request.getEmail() !=null && !request.getEmail().isBlank()){
            newPatient.setEmail(stringEncryptor.encrypt(request.getEmail().toLowerCase()));
        }else{
            newPatient.setEmail(null);
        }
        newPatient.setGender(stringEncryptor.encrypt(request.getGender()));
        newPatient.setBirthDate(Date.valueOf(request.getBirthDate()));
        if(request.getPhone() !=null && !request.getPhone().isBlank()) {
            newPatient.setPhoneNumber(stringEncryptor.encrypt(request.getPhone()));
        }else{
            newPatient.setPhoneNumber(null);
        }
        newPatient.setBloodGroup(stringEncryptor.encrypt(request.getBlood()));
        if(request.getAddress()!=null && !request.getAddress().isBlank()){
            newPatient.setAddress(stringEncryptor.encrypt(request.getAddress()));
        }else{
            newPatient.setAddress(null);
        }
        //While registering, it would be OP by default
        newPatient.setPatientType("OP");
        //Assigning empty wardNo at the beginning
        newPatient.setWardNo("");
        return newPatient;
    }

    public DashboardResponse getHome(String userId) {
        DashboardResponse resp = new DashboardResponse();
        try{
            if(userId==null || userId.isBlank()){
                throw new RequestValidationException("Empty value receives in userId");
            }

            Optional<Login> optLogin = loginRepo.findAccountByUserId(userId, Roles.RECEPTIONIST.toString());
            if(optLogin.isEmpty()){
                throw new NoSuchAccountException("Invalid userId passed in the request");
            }
            String email = optLogin.get().getUsername();
            Optional<Receptionist> optReception = receptionRepo.findById(userId);
            if (optReception.isEmpty()) {
                resp.setError("Username not found");
            } else {
                PersonalDetail detail = getDetailForReceptionist(optReception.get());
                detail.setEmail(email);
                resp.setDetail(detail);
            }

        } catch (RequestValidationException e){
            log.error("getPersonalDetail | RequestValidationException occurred: "+e.getMessage());
            resp.setError(e.getMessage());
        } catch(NoSuchAccountException e){
            log.error("getPersonalDetail | NoSuchAccountException occurred: "+e.getMessage());
            resp.setError(e.getMessage());
        } catch(Exception e){
            log.error("getPersonalDetail | Exception occurred: "+e.getMessage());
            resp.setError(e.getMessage());
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
        if(receptionist.getProfileImage() != null && !receptionist.getProfileImage().isBlank()){
            obj.setProfileImage(fileService.loadUserImage(receptionist.getProfileImage()));
        }
        return obj;
    }

}