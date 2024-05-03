package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.entity.Emergency;
import org.his.entity.user.Doctor;
import org.his.entity.user.Patient;
import org.his.exception.NoSuchAccountException;
import org.his.exception.RequestValidationException;
import org.his.repo.AdmitRepo;
import org.his.repo.EmergencyRepo;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.PatientRepo;
import org.his.util.ShiftUtility;
import org.his.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DoctorService {

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private FilesStorageService fileService;

    @Autowired
    private AdmitRepo admitRepo;

    @Autowired
    private EmergencyRepo emergencyRepo;

    //Not being used
    public PatientResponse viewPastPatients(String userId) {
        PatientResponse response = new PatientResponse();

        List<Patient> patients = new ArrayList<>();
        // Check if the nurse is head nurse

        patients.addAll(patientRepo.findAll());
        if (patients.isEmpty()) {
            response.setError("No nurses available.");
            return response;
        }
        List<PatientDetail> patientDetails = new ArrayList<>();

        // Iterate through the list of nurses and map their details
        for (Patient patient : patients) {

                PatientDetail detail = new PatientDetail();
                detail.setAadhaar(patient.getAadhar());
                detail.setFirstName(patient.getFirstName());
                detail.setLastName(patient.getLastName());
//                detail.setEmail(patient.getEmail());
//                detail.setPhone(patient.getPhoneNumber());
//                detail.setGender(patient.getGender());
//                detail.setBlood(patient.getBloodGroup());
//                detail.setAddress(patient.getAddress());
//                detail.setBirthDate(patient.getBirthDate().toString());


                patientDetails.add(detail);

        }

        response.setResponse(patientDetails);

        return response;
    }


    public DashboardResponse getDashBoard(String userId) {
        DashboardResponse response = new DashboardResponse();

        try{
            if(userId == null || userId.isBlank()){
                throw new RequestValidationException("Empty doctorId passed in the request");
            }

            Optional<Doctor> optDoc = doctorRepo.findById(userId);
            if (optDoc.isEmpty()) {
                throw new NoSuchAccountException("No such doctor found in the table");
            }else{
                PersonalDetail detail = getDetailForDoc(optDoc.get());
                response.setDetail(detail);

                Shift shift = getShiftForDoc(optDoc.get());
                response.setShift(shift);

            }
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            if(ShiftUtility.isDoctorOnShift(optDoc.get(),hour, currentDayOfWeek)){
                response.setOnDuty(1);
                int ipPatientCount = admitRepo.countAdmitByActiveIsTrueAndPatientType("IP");
                int opPatientCount = admitRepo.countAdmitByActiveIsTrueAndPatientType("OP");

                response.setIpPatient(ipPatientCount);
                response.setOpPatient(opPatientCount);

                List<Emergency> list = emergencyRepo.findAllByHandledIsFalseOrderByDateDesc();
//                log.info("size of emer:"+list.size());
                response.setEmergencies(getListFromEmergencyList(list));
            }

            log.info("getDashBoard | request processed successfully.");
        } catch (NoSuchAccountException e){
            log.error("NoSuchAccountException | Exception occurred: "+e.getMessage());
            response.setError(e.getMessage());
        } catch (RequestValidationException e){
            log.error("RequestValidationException | Exception occurred: "+e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e){
            log.error("getDashBoard | Exception occurred: "+e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    private List<EmergencyItem> getListFromEmergencyList(List<Emergency> list) {
        List<EmergencyItem> itemList = new ArrayList<>();
        for(Emergency bean : list){
            EmergencyItem item = new EmergencyItem();
            item.setEmerId(bean.getEmerId());
            item.setDoctorId(bean.getDoctorId());
            item.setHandled(bean.isHandled());
            item.setRemark(bean.getRemark());
            item.setTimestamp(Utility.getFormattedOffsetTime(bean.getDate()));

            itemList.add(item);
        }
        return itemList;
    }

    private Shift getShiftForDoc(Doctor doctor) {
        Shift obj = new Shift();
        obj.setMon(doctor.getMon());
        obj.setTue(doctor.getTue());
        obj.setWed(doctor.getWed());
        obj.setThu(doctor.getThu());
        obj.setFri(doctor.getFri());
        obj.setSat(doctor.getSat());
        obj.setSun(doctor.getSun());
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
        obj.setActive(true);

        //Uncomment below for testing
        //obj.setProfileImage(fileService.loadUserImage(doctor.getProfileImage()));
        return obj;
    }

    public GeneralResp handleEmergency(String userId, String emerId) {
        GeneralResp resp = new GeneralResp();
        try{

            if(userId==null || userId.isBlank() || emerId==null || emerId.isBlank()){
                throw new RequestValidationException("Empty value received in request");
            }

            Doctor doc = doctorRepo.findById(userId).orElse(null);
            if (doc == null) {
                throw new NoSuchAccountException("No such doctor found in the table");
            }

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            if(doc.isHead() || ShiftUtility.isDoctorOnShift(doc,hour, currentDayOfWeek)){

                Emergency emergency = emergencyRepo.findById(emerId).orElse(null);
                if(emergency == null){
                    throw new Exception("No such emergencyId exists in the table");
                }

                if(emergency.isHandled()){
                    throw new Exception("This emergency is already handled by another doctor");
                }
                emergency.setDate(OffsetDateTime.now());
                emergency.setDoctorId(userId);
                emergency.setHandled(true);

                emergencyRepo.save(emergency);

            }else{
                throw new Exception("Doctor is either not head-doctor or not on shift to handle the emergency");
            }

            resp.setResponse("SUCCESS");
            log.info("handleEmergency | request processed successfully");
        } catch (RequestValidationException e){
            log.error("handleEmergency | RequestValidationException occurred: "+e.getMessage());
            resp.setResponse("FAILED");
            resp.setError(e.getMessage());
        } catch (NoSuchAccountException e){
            log.error("handleEmergency | NoSuchAccountException occurred: "+e.getMessage());
            resp.setResponse("FAILED");
            resp.setError(e.getMessage());
        } catch (Exception e){
            log.error("handleEmergency | Exception occurred: "+e.getMessage());
            resp.setResponse("FAILED");
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
