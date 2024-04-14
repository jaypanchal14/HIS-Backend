package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PatientDetail;
import org.his.bean.PatientResponse;
import org.his.entity.Admit;
import org.his.entity.user.Doctor;
import org.his.entity.user.Nurse;
import org.his.entity.user.Patient;
import org.his.exception.RequestValidationException;
import org.his.repo.AdmitRepo;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.NurseRepo;
import org.his.repo.user.PatientRepo;
import org.his.util.ShiftUtility;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class PatientService {

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private NurseRepo nurseRepo;

    @Autowired
    private AdmitRepo admitRepo;

    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

    public PatientResponse viewLivePatients(String role, String userId, int isOP) {
        PatientResponse response = new PatientResponse();
        List<PatientDetail> livePatients;
        List<Admit> admits;
        try {

            if(role == null || role.isBlank() || userId == null || userId.isBlank()){
                throw new RequestValidationException("Empty role or userId passed in the request");
            }
            role = role.toUpperCase();

            if(isOP == 1){
                //Only accessible by DOCTOR
                if (!role.equals("DOCTOR")) {
                    throw new RequestValidationException("Unsupported role passed in the request");
                }

                if (!isValidDoctor(userId)) {
                    response.setError("Invalid Doctor credentials or unauthorized access.");
                    return response;
                }

                admits = admitRepo.findAllByActiveAndPatientType(true, "OP");
                livePatients = getLivePatientsFromAdmitList(admits);

            } else if (isOP == 0) {
                //accessible by both NURSE and DOCTOR
                if (!role.equals("DOCTOR") && !role.equals("NURSE")) {
                    throw new RequestValidationException("Unsupported role passed in the request");
                }

                if (role.equals("NURSE") && !isValidNurse(userId)) {
                    throw new RequestValidationException("Invalid nurse credentials or unauthorized access.");
                }
                if (role.equals("DOCTOR") && !isValidDoctor(userId)) {
                    throw new RequestValidationException("Invalid Doctor credentials or unauthorized access.");
                }

                admits = admitRepo.findAllByActiveAndPatientType(true, "IP");
                livePatients = getLivePatientsFromAdmitList(admits);

            } else{
                throw new RequestValidationException("Invalid value passed in isOP parameter");
            }

            // Set the response
            response.setResponse(livePatients);
            log.info("viewLivePatients | request processed successfully.");

        } catch (RequestValidationException e){
            log.error("viewLivePatients | RequestValidationException occurred while fetching live patients: "+e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e){
            log.error("viewLivePatients | Exception occurred while fetching live patients: "+e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    private List<PatientDetail> getLivePatientsFromAdmitList(List<Admit> admits) {
        List<PatientDetail> list = new ArrayList<>();
        Set<String> patientId = new HashSet<>();
        Map<String, Admit> patientMapping = new HashMap<>();

        //Tmp use
        PatientDetail detail = null;
        Admit admit = null;

        for(Admit obj : admits){
            if(patientId.contains(obj.getPatientId())){
                continue;
            }
            patientId.add(obj.getPatientId());
            patientMapping.put(obj.getPatientId(), obj);
        }

        List<Patient> patientList = patientRepo.findAllByAadharIn(patientId);

        for(Patient obj : patientList){
            admit = patientMapping.get(obj.getAadhar());

            detail = new PatientDetail();
            detail.setAadhaar(obj.getAadhar());
            detail.setAdmitId(admit.getAdmitId());
            detail.setRemark(stringEncryptor.decrypt(admit.getRemark()));
            detail.setFirstName(obj.getFirstName());
            detail.setLastName(obj.getLastName());
            detail.setPhone(stringEncryptor.decrypt(obj.getPhoneNumber()));
            detail.setGender(stringEncryptor.decrypt(obj.getGender()));
            detail.setBlood(stringEncryptor.decrypt(obj.getBloodGroup()));
            detail.setWardNo(obj.getWardNo());
            //detail.setAddress(stringEncryptor.decrypt(obj.getAddress()));
            //detail.setBirthDate(obj.getBirthDate().toString());

            list.add(detail);
        }
        return list;
    }

    private boolean isValidNurse(String nurseId) {
        Nurse nurse = nurseRepo.findById(nurseId).orElse(null);
        if(nurse == null){
            return false;   // Nurse ID not found
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        int hour = now.getHour();
        int currentDayOfWeek = now.getDayOfWeek().getValue();
        return ShiftUtility.isNurseOnShift(nurse, hour, currentDayOfWeek);
    }
    private boolean isValidDoctor(String doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if(doctor == null){
            return false;      // Doctor ID not found
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        int hour = now.getHour();
        int currentDayOfWeek = now.getDayOfWeek().getValue();
        return ShiftUtility.isDoctorOnShift(doctor, hour, currentDayOfWeek);
    }

    public PatientResponse viewOneLivePatient(String role, String userId, String patientId) {
        PatientResponse response = new PatientResponse();

        if(role.equalsIgnoreCase("NURSE"))
        {
            if (isValidNurse(userId)) {
                response.setError("Invalid nurse credentials or unauthorized access.");
                return response;
            }
        }
        else {
            if (isValidDoctor(userId)) {
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
            patientDetail.setAadhaar(patient.getAadhar());
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