package org.his.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.entity.Admit;
import org.his.entity.Diagnosis;
import org.his.entity.Prescription;
import org.his.entity.user.Doctor;
import org.his.entity.user.Nurse;
import org.his.entity.user.Patient;
import org.his.exception.NoSuchAccountException;
import org.his.exception.RequestValidationException;
import org.his.repo.AdmitRepo;
import org.his.repo.DiagnosisRepo;
import org.his.repo.PrescriptionRepo;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.NurseRepo;
import org.his.repo.user.PatientRepo;
import org.his.util.ShiftUtility;
import org.his.util.Utility;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private DiagnosisRepo diagnosisRepo;

    @Autowired
    private PrescriptionRepo prescriptionRepo;

    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

    @Autowired
    private FilesStorageService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    public PatientResponse viewLivePatients(String role, String userId, int isOP) {
        PatientResponse response = new PatientResponse();
        List<PatientDetail> livePatients = new ArrayList<>();
        List<Admit> admits;
        try {

            if (role == null || role.isBlank() || userId == null || userId.isBlank()) {
                throw new RequestValidationException("Empty role or userId passed in the request");
            }
            role = role.toUpperCase();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();

            if (isOP == 1) {
                //Only accessible by DOCTOR
                if (!role.equals("DOCTOR")) {
                    throw new RequestValidationException("Unsupported role passed in the request");
                }

                Doctor doctor = doctorRepo.findById(userId).orElse(null);
                if (doctor == null) {
                    throw new RequestValidationException("Doctor not found in the doctor table");
                }

                if(doctor.isHead() || ShiftUtility.isDoctorOnShift(doctor, hour, currentDayOfWeek)){
                    admits = admitRepo.findAllByActiveAndPatientType(true, "OP");
                    livePatients = getLivePatientsFromAdmitList(admits);
                }else{
                    log.info("Doctor is not on the shift hour:" +hour+", day:"+currentDayOfWeek);
                }

            } else if (isOP == 0) {
                //accessible by both NURSE and DOCTOR

                if (!role.equals("DOCTOR") && !role.equals("NURSE")) {
                    throw new RequestValidationException("Unsupported role passed in the request");
                }

                if (role.equals("NURSE")) {
                    Nurse nurse = nurseRepo.findById(userId).orElse(null);
                    if (nurse == null) {
                        throw new RequestValidationException("nurse not found in the nurse table");
                    }
                    if(nurse.isHead() || ShiftUtility.isNurseOnShift(nurse, hour, currentDayOfWeek)){
                        admits = admitRepo.findAllByActiveAndPatientType(true, "IP");
                        livePatients = getLivePatientsFromAdmitList(admits);    
                    }else{
                        log.info("Nurse is not on the shift hour:" +hour+", day:"+currentDayOfWeek);
                    }
                }else{
                    //For doctor
                    Doctor doctor = doctorRepo.findById(userId).orElse(null);
                    if (doctor == null) {
                        throw new RequestValidationException("Invalid Doctor credentials or unauthorized access.");
                    }
                    if(doctor.isHead() || ShiftUtility.isDoctorOnShift(doctor, hour, currentDayOfWeek)){
                        admits = admitRepo.findAllByActiveAndPatientType(true, "IP");
                        livePatients = getLivePatientsFromAdmitList(admits);
                    }else{
                        log.info("Doctor is not on the shift hour:" +hour+", day:"+currentDayOfWeek);
                    }
                }

            } else {
                throw new RequestValidationException("Invalid value passed in isOP parameter");
            }

            // Set the response
            response.setResponse(livePatients);
            log.info("viewLivePatients | request processed successfully.");

        } catch (RequestValidationException e) {
            log.error("viewLivePatients | RequestValidationException occurred while fetching live patients: " + e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("viewLivePatients | Exception occurred while fetching live patients: " + e.getMessage());
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

        for (Admit obj : admits) {
            if (patientId.contains(obj.getPatientId())) {
                continue;
            }
            patientId.add(obj.getPatientId());
            patientMapping.put(obj.getPatientId(), obj);
        }

        List<Patient> patientList = patientRepo.findAllByAadharIn(patientId);

        for (Patient obj : patientList) {
            admit = patientMapping.get(obj.getAadhar());

            detail = new PatientDetail();
            detail.setAadhaar(obj.getAadhar());
            detail.setAdmitId(admit.getAdmitId());
            if(admit.getRemark()!=null && !admit.getRemark().isBlank()){
                detail.setRemark(stringEncryptor.decrypt(admit.getRemark()));
            }else{
                detail.setRemark("");
            }
            detail.setFirstName(obj.getFirstName());
            detail.setLastName(obj.getLastName());
            if(obj.getPhoneNumber()!=null && !obj.getPhoneNumber().isBlank()){
                detail.setPhone(stringEncryptor.decrypt(obj.getPhoneNumber()));
            }else{
                detail.setPhone("");
            }
            detail.setGender(stringEncryptor.decrypt(obj.getGender()));
            detail.setBlood(stringEncryptor.decrypt(obj.getBloodGroup()));
            detail.setWardNo(obj.getWardNo());
            //detail.setAddress(stringEncryptor.decrypt(obj.getAddress()));
            //detail.setBirthDate(obj.getBirthDate().toString());
            list.add(detail);
        }
        return list;
    }

    private void validateRoleAndUserId(String role, String userId) throws RequestValidationException {
        if (!role.equals("DOCTOR") && !role.equals("NURSE")) {
            throw new RequestValidationException("Unsupported role passed in the request");
        }

        if (role.equals("NURSE") && !isValidNurse(userId)) {
            throw new RequestValidationException("Invalid nurse credentials or unauthorized access.");
        }
        if (role.equals("DOCTOR") && !isValidDoctor(userId)) {
            throw new RequestValidationException("Invalid Doctor credentials or unauthorized access.");
        }
    }

    private boolean isValidNurse(String nurseId) {
        Nurse nurse = nurseRepo.findById(nurseId).orElse(null);
        if (nurse == null) {
            return false;   // Nurse ID not found
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        int hour = now.getHour();
        int currentDayOfWeek = now.getDayOfWeek().getValue();
        return nurse.isHead() || ShiftUtility.isNurseOnShift(nurse, hour, currentDayOfWeek);
    }

    private boolean isValidDoctor(String doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) {
            return false;      // Doctor ID not found
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        int hour = now.getHour();
        int currentDayOfWeek = now.getDayOfWeek().getValue();
        return doctor.isHead() || ShiftUtility.isDoctorOnShift(doctor, hour, currentDayOfWeek);
    }

    public OnePatientResponse viewOneLivePatient(String role, String userId, String admitId) {
        OnePatientResponse response = new OnePatientResponse();
        try {

            if (role == null || role.isBlank() || userId == null || userId.isBlank()) {
                throw new RequestValidationException("Empty role or userId passed in the request");
            }
            role = role.toUpperCase();

            validateRoleAndUserId(role, userId);

            Optional<Admit> admitOptional = admitRepo.findByAdmitIdAndActiveIsTrue(admitId);
            if (admitOptional.isPresent()) {
                Admit admit = admitOptional.get();
                // Fetch patient details using admit and patient bean
                PatientDetail patientDetail = getPatientDetailsById(admit);
                if (patientDetail != null) {
                    response.setDetail(patientDetail);
                }
                List<DiagnosisItem> list = getDiagnosisListForAdmitId(admit);
                response.setList(list);

            } else {
                log.info("viewOneLivePatient | request processed successfully but no active patient found.");
            }
            log.info("viewOneLivePatient | request processed successfully.");

        } catch (RequestValidationException e) {
            log.error("viewOneLivePatient | RequestValidationException occurred while fetching live patients: " + e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("viewOneLivePatient | exception occurred: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    private PatientDetail getPatientDetailsById(Admit admit) {
        Optional<Patient> patientOptional = patientRepo.findById(admit.getPatientId());
        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            PatientDetail patientDetail = getPatientDetailFromPatientBean(patient);
            patientDetail.setAdmitId(admit.getAdmitId());
            patientDetail.setPatientType(admit.getPatientType());
            return patientDetail;
        }
        return null;
    }

    private PatientDetail getPatientDetailFromPatientBean(Patient patient) {
        PatientDetail patientDetail = new PatientDetail();

        patientDetail.setAadhaar(patient.getAadhar());
        patientDetail.setFirstName(patient.getFirstName());
        patientDetail.setLastName(patient.getLastName());
        if(patient.getEmail() !=null && !patient.getEmail().isBlank()){
            patientDetail.setEmail(stringEncryptor.decrypt(patient.getEmail()));
        }else{
            patientDetail.setEmail(null);
        }
        if(patient.getPhoneNumber() !=null && !patient.getPhoneNumber().isBlank()){
            patientDetail.setPhone(stringEncryptor.decrypt(patient.getPhoneNumber()));
        }else{
            patientDetail.setPhone("");
        }
        patientDetail.setGender(stringEncryptor.decrypt(patient.getGender()));
        patientDetail.setBlood(stringEncryptor.decrypt(patient.getBloodGroup()));
        if(patient.getAddress()!=null && !patient.getAddress().isBlank()){
            patientDetail.setAddress(stringEncryptor.decrypt(patient.getAddress()));
        }else{
            patientDetail.setAddress("");
        }
        patientDetail.setBirthDate(patient.getBirthDate().toString());
        patientDetail.setWardNo(patient.getWardNo());

        //patientDetail.setPatientImage(fileService.loadPatientImage(patient.getProfileImage()));

        return patientDetail;
    }

    public GeneralResp addDiagnosis(String payload, MultipartFile file, String role, String userId) {
        GeneralResp resp = new GeneralResp();
        DiagnosisItem request;
        String diagnosisFile = null;
        boolean isFilePresent = false;
        Prescription prescription;
        Diagnosis diagnosis;
        try{

            if (role == null || role.isBlank() || userId == null || userId.isBlank()) {
                throw new RequestValidationException("Empty role or userId passed in the request");
            }

            role = role.toUpperCase();
            validateRoleAndUserId(role, userId);

            request = objectMapper.readValue(payload, DiagnosisItem.class);

            if(request.getAdmitId() == null || request.getAdmitId().isBlank()){
                throw new RequestValidationException("Empty admitId passed in the request");
            }

            Optional<Admit> optAdmit = admitRepo.findByAdmitIdAndActiveIsTrue(request.getAdmitId());
            if(optAdmit.isEmpty()){
                throw new RequestValidationException("Invalid/Inactive admitId passed in the request");
            }

            //Save file only if it's passed in the request
            if(file != null && !file.isEmpty()){
                isFilePresent = true;

                String extension = Utility.getFileExtension(file);
                diagnosisFile = Utility.generateNewImageName(extension);
            }

            diagnosis = getDiagnosisForNewRequest(request, role, userId);
            diagnosis.setFile(diagnosisFile);

            //Only save prescription if there are any medicines mentioned
            if(request.getMedicine() != null && !request.getMedicine().isEmpty()){
                prescription = getPrescriptionForNewRequest(diagnosis, request);
                prescriptionRepo.save(prescription);
            }
            diagnosisRepo.save(diagnosis);

            //TODO : Handle case of discharge based on discharge variable of request
            if(request.getDischarge() == 1){
                Integer count = admitRepo.updateAdmitStatus(request.getAdmitId(), false);
                if(count==1){
                    log.info("addDiagnosis | marked the patient as inActive in admit table");
                }else{
                    log.error("addDiagnosis | unable to mark the patient as inActive in admit table");
                }
            }

            if(isFilePresent){
                fileService.savePatientDiagnosis(file, diagnosisFile);
            }

            resp.setResponse("SUCCESS");
            log.info("addDiagnosis | Diagnosis added successfully.");
        } catch (RequestValidationException e) {
            log.error("addDiagnosis | RequestValidationException occurred: " + e.getMessage());
            resp.setError(e.getMessage());
            resp.setResponse("FAILED");
        } catch (Exception e){
            log.error("addDiagnosis | Exception occurred: "+e.getMessage());
            resp.setError(e.getMessage());
            resp.setResponse("FAILED");
        }
        return resp;
    }

    private Prescription getPrescriptionForNewRequest(Diagnosis diagnosis, DiagnosisItem request) throws JsonProcessingException {
        Prescription obj = new Prescription();
        obj.setPresId(Utility.getUniqueId());
        obj.setPatientId(request.getPatientId());
        obj.setRole(diagnosis.getRole());
        obj.setUserId(diagnosis.getUserId());
        obj.setDiagnosisId(diagnosis.getDiagnosisId());
        obj.setPharmaId(null);
        if(request.getMedicine() != null && !request.getMedicine().isEmpty()){
            obj.setMedicine(objectMapper.writeValueAsString(request.getMedicine()));
        }else{
            obj.setMedicine(null);
        }
        return obj;
    }

    private Diagnosis getDiagnosisForNewRequest(DiagnosisItem request, String role, String userId) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setDiagnosisId(Utility.getUniqueId());
        if(request.getRemarks()!=null && !request.getRemarks().isBlank()){
            diagnosis.setRemark(stringEncryptor.encrypt(request.getRemarks()));
        }else{
            diagnosis.setRemark(null);
        }
        diagnosis.setAdmitId(request.getAdmitId());
        diagnosis.setRole(role);
        diagnosis.setUserId(userId);

        return diagnosis;
    }

    public DiagnosisResponse getDiagnosisForAdmitId(String role, String admitId, String userId) {
        DiagnosisResponse resp = new DiagnosisResponse();
        List<DiagnosisItem> itemList;

        try{
            if (role == null || role.isBlank() || userId == null || userId.isBlank() || admitId == null || admitId.isBlank()) {
                throw new RequestValidationException("Empty role or userId or admitId passed in the request");
            }

            role = role.toUpperCase();
            validateRoleAndUserId(role, userId);

            //Check if admitId is valid or not
            Optional<Admit> optAdmit = admitRepo.findById(admitId);
            if(optAdmit.isEmpty()){
                throw new Exception("No such admitId exists in the database");
            }
            Admit admit = optAdmit.get();

            itemList = getDiagnosisListForAdmitId(admit);

            resp.setResponse(itemList);
            log.info("getDiagnosis | request processed successfully.");
        } catch (RequestValidationException e) {
            log.error("getDiagnosis | RequestValidationException occurred: " + e.getMessage());
            resp.setError(e.getMessage());
        } catch (Exception e){
            log.error("getDiagnosis | Exception occurred: "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }

    private List<DiagnosisItem> getDiagnosisListForAdmitId(Admit admit) throws JsonProcessingException {

        List<DiagnosisItem> itemList = new ArrayList<>();
        List<Diagnosis> list = diagnosisRepo.findAllByAdmitIdOrderByDateDesc(admit.getAdmitId());
        if(list.isEmpty()){
            return itemList;
        }
        Map<String, Prescription> mapping = new HashMap<>();
        String tmp;
        Set<String> listOfDiagnosisId = new HashSet<>();

        for(Diagnosis obj : list){
            listOfDiagnosisId.add(obj.getDiagnosisId());
        }
        List<Prescription> prescriptionList = prescriptionRepo.findAllByDiagnosisIdIn(listOfDiagnosisId);
        for(Prescription p : prescriptionList){
            mapping.put(p.getDiagnosisId(), p);
        }

        for(Diagnosis obj : list){
            DiagnosisItem item = getDiagnosisItemFromBean(obj);
            item.setPatientId(admit.getPatientId());
            if(mapping.containsKey(obj.getDiagnosisId())){
                tmp = mapping.get(obj.getDiagnosisId()).getMedicine();
                if(tmp!=null && !tmp.isBlank()){
                    item.setMedicine(objectMapper.readValue(tmp, new TypeReference<Map<String, Integer>>(){}));
                }else{
                    Map<String, Integer> map = new HashMap<>();
                    item.setMedicine(map);
                }
            }
            itemList.add(item);
        }

        return itemList;
    }

    private DiagnosisItem getDiagnosisItemFromBean(Diagnosis obj) {
        DiagnosisItem item = new DiagnosisItem();
        item.setDiagnosisId(obj.getDiagnosisId());
        item.setAdmitId(obj.getAdmitId());
        item.setFile(obj.getFile());
        item.setDate(Utility.getFormattedOffsetTime(obj.getDate()));
        if(obj.getRemark()!=null && !obj.getRemark().isBlank()){
            item.setRemarks(stringEncryptor.decrypt(obj.getRemark()));
        }else{
            item.setRemarks("");
        }
        return item;
    }

    public OnePatientResponse pastHistoryOnePatient(String role, String patientId, String userId) {
        OnePatientResponse resp = new OnePatientResponse();
        List<DiagnosisItem> list = new ArrayList<>();
        DiagnosisItem item;
        List<Admit> admits;
        Nurse nurse;
        Doctor doctor;
        boolean isHead = false;
        try{
            if (role == null || role.isBlank() || userId == null || userId.isBlank() ) {
                throw new RequestValidationException("Empty role or userId passed in the request");
            }
            role = role.toUpperCase();

            if("NURSE".equals(role)){
                nurse = nurseRepo.findById(userId).orElse(null);
                if (nurse == null) {
                    throw new NoSuchAccountException("Nurse not found in the nurse table");
                }
                if(!nurse.isHead()){
                    log.error("pastHistoryOnePatient | Nurse is not head-nurse and still trying to view past-history, terminating the request");
                    resp.setList(list);
                    return resp;
                }else{
                    isHead = true;
                }
            } else if ("DOCTOR".equals(role)) {
                doctor = doctorRepo.findById(userId).orElse(null);
                if (doctor == null) {
                    throw new NoSuchAccountException("Invalid Doctor credentials or unauthorized access.");
                }
                if(!doctor.isHead()){
                    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                    int hour = now.getHour();
                    int currentDayOfWeek = now.getDayOfWeek().getValue();
                    if(!ShiftUtility.isDoctorOnShift(doctor, hour, currentDayOfWeek)){
                        log.info("pastHistoryOnePatient | doctor is not on the shift to view past-history");
                        resp.setList(list);
                        return resp;
                    }
                }else {
                    isHead = true;
                }
            } else {
                throw new RequestValidationException("role other than DOCTOR/NURSE passed int the request");
            }

            Patient patient = patientRepo.findById(patientId).orElse(null);
            if(patient == null){
                throw new NoSuchAccountException("No such patient exists in the database");
            }
            resp.setDetail(getPatientDetailFromPatientBean(patient));

            if(isHead){
                //Fetch all admit-rows
                admits = admitRepo.findAllByPatientId(patientId);

            }else{
                //Fetch only consulted row
                List<String> admitIds = diagnosisRepo.findDistinctAdmitIdByUserId("DOCTOR", userId);
                admits = admitRepo.findAllByAdmitIdInOrderByDateDesc(admitIds);
            }

            for(Admit obj : admits){
                item = new DiagnosisItem();
                item.setAdmitId(obj.getAdmitId());
                item.setDate(Utility.getFormattedOffsetTime(obj.getDate()));
                if(obj.getRemark() != null && !obj.getRemark().isBlank()){
                    item.setRemarks(stringEncryptor.decrypt(obj.getRemark()));
                }else{
                    item.setRemarks("");
                }
                item.setPatientId(obj.getPatientId());

                list.add(item);
            }

            resp.setList(list);
            log.info("pastHistoryOnePatient | request processed successfully");
        } catch (NoSuchAccountException e) {
            log.error("pastHistoryOnePatient | NoSuchAccountException occurred: " + e.getMessage());
            resp.setError(e.getMessage());
        } catch (RequestValidationException e) {
            log.error("pastHistoryOnePatient | RequestValidationException occurred: " + e.getMessage());
            resp.setError(e.getMessage());
        } catch (Exception e){
            log.error("pastHistoryOnePatient | Exception occurred: "+e.getMessage());
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public PatientResponse pastHistory(String role, String userId, String patientId) {
        PatientResponse resp = new PatientResponse();
        List<PatientDetail> patientList = new ArrayList<>();
        List<Patient> patients = new ArrayList<>();
        PatientDetail detail;
        Nurse nurse;
        Doctor doctor;
        boolean isHead = false;
        try{

            if (role == null || role.isBlank() || userId == null || userId.isBlank() ) {
                throw new RequestValidationException("Empty role or userId passed in the request");
            }
            role = role.toUpperCase();
            if("NURSE".equals(role)){
                nurse = nurseRepo.findById(userId).orElse(null);
                if (nurse == null) {
                    throw new NoSuchAccountException("Nurse not found in the nurse table");
                }
                if(!nurse.isHead()){
                    log.error("pastHistory | Nurse is not head-nurse and still trying to view past-history, terminating the request");
                    resp.setResponse(patientList);
                    return resp;
                }else{
                    isHead = true;
                }
            } else if ("DOCTOR".equals(role)) {
                doctor = doctorRepo.findById(userId).orElse(null);
                if (doctor == null) {
                    throw new NoSuchAccountException("Invalid Doctor credentials or unauthorized access.");
                }
                if(!doctor.isHead()){
                    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                    int hour = now.getHour();
                    int currentDayOfWeek = now.getDayOfWeek().getValue();
                    if(!ShiftUtility.isDoctorOnShift(doctor, hour, currentDayOfWeek)){
                        log.info("pastHistory | doctor is not on the shift to view past-history");
                        resp.setResponse(patientList);
                        return resp;
                    }
                }else {
                    isHead = true;
                }
            } else {
                throw new RequestValidationException("role other than DOCTOR/NURSE passed int the request");
            }

            if(patientId != null && !patientId.isBlank()){
                if(isHead) {
                    Patient patient = patientRepo.findById(patientId).orElse(null);
                    if(patient == null){
                        log.info("pastHistory | patientId not found in the database");
                        resp.setResponse(patientList);
                        return resp;
                    }
                    patients.add(patient);
                }else{
                    List<String> admitIds = diagnosisRepo.findDistinctAdmitIdByUserId("DOCTOR", userId);
                    List<String> pListFromAdmit = admitRepo.findDistinctPatientIdByAdmitId(admitIds);

                    if(pListFromAdmit.stream().noneMatch(item -> item.equals(patientId))){
                        log.info("pastHistory | doctor has not treated this patient");
                        resp.setResponse(patientList);
                        return resp;
                    }
                    Patient patient = patientRepo.findById(patientId).orElse(null);
                    if(patient == null){
                        log.info("pastHistory | patientId not found in the database");
                        resp.setResponse(patientList);
                        return resp;
                    }
                    patients.add(patient);
                }

            }else{
                if(isHead){
                    //Fetch all the patients details
                    patients = patientRepo.findAll();

                }else{
                    //Fetch patients treated by given doctor
                    List<String> admitIds = diagnosisRepo.findDistinctAdmitIdByUserId("DOCTOR", userId);
                    List<String> pListFromAdmit = admitRepo.findDistinctPatientIdByAdmitId(admitIds);
                    Set<String> patientIds = new HashSet<>(pListFromAdmit);
                    patients = patientRepo.findAllByAadharIn(patientIds);
                }
            }



            for (Patient obj : patients) {

                detail = new PatientDetail();
                detail.setAadhaar(obj.getAadhar());
                detail.setFirstName(obj.getFirstName());
                detail.setLastName(obj.getLastName());
                if(obj.getPhoneNumber()!=null && !obj.getPhoneNumber().isBlank()){
                    detail.setPhone(stringEncryptor.decrypt(obj.getPhoneNumber()));
                }else{
                    detail.setPhone("");
                }
                detail.setGender(stringEncryptor.decrypt(obj.getGender()));
                detail.setBlood(stringEncryptor.decrypt(obj.getBloodGroup()));
                detail.setWardNo(obj.getWardNo());
                detail.setBirthDate(obj.getBirthDate().toString());
                patientList.add(detail);
            }
            resp.setResponse(patientList);
            log.info("pastHistory | request processed successfully");
        } catch (NoSuchAccountException e) {
            log.error("pastHistory | NoSuchAccountException occurred: " + e.getMessage());
            resp.setError(e.getMessage());
        } catch (RequestValidationException e) {
            log.error("pastHistory | RequestValidationException occurred: " + e.getMessage());
            resp.setError(e.getMessage());
        } catch (Exception e){
            log.error("pastHistory | Exception occurred: "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }
}