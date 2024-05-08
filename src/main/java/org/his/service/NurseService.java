package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.entity.Admit;
import org.his.entity.Login;
import org.his.entity.Ward;
import org.his.entity.WardHistory;
import org.his.entity.user.Nurse;
import org.his.entity.user.Patient;
import org.his.exception.NoSuchAccountException;
import org.his.exception.RequestValidationException;
import org.his.repo.AdmitRepo;
import org.his.repo.LoginRepo;
import org.his.repo.WardHistoryRepo;
import org.his.repo.WardRepo;
import org.his.repo.user.NurseRepo;
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
public class NurseService {

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private NurseRepo nurseRepo;

    @Autowired
    private WardRepo wardRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private FilesStorageService fileService;

    @Autowired
    private AdmitRepo admitRepo;

    @Autowired
    private WardHistoryRepo wardHistoryRepo;

    public ReceptionDetailResp getOnShiftNurses(String userId) {
        log.info("getOnShiftNurses | request received to view on-shift nurses.");
        ReceptionDetailResp response = new ReceptionDetailResp();
        try {
            Nurse nur = nurseRepo.findById(userId).orElse(null);
            if (nur == null || !nur.isHead()) {
                response.setError("Nurse is not found in the table or doesn't have the privilege to see.");
                return response;
            }

            List<ViewUserIdentifier> identifiers = loginRepo.getActiveUsersBasedOnRole("NURSE");
            List<String> nurseIds = new ArrayList<>();
            for (ViewUserIdentifier identifier : identifiers) {
                if (identifier.getUserId().equals(userId)) {
                    continue;
                }
                nurseIds.add(identifier.getUserId());
            }
            List<Nurse> nurses = nurseRepo.findAllByIdIn(nurseIds);

            List<PersonalDetail> nurseDetails = new ArrayList<>();

            if (nurses == null || nurses.isEmpty()) {
                response.setResponse(nurseDetails);
                return response;
            }

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            // Iterate through the list of nurses and map their details
            for (Nurse nurse : nurses) {
                if (ShiftUtility.isNurseOnShift(nurse, hour, currentDayOfWeek)) {
                    PersonalDetail detail = new PersonalDetail();
                    detail.setFirstName(nurse.getFirstName());
                    detail.setLastName(nurse.getLastName());
                    detail.setPhone(nurse.getPhoneNumber());
                    detail.setGender(nurse.getGender());
                    detail.setSpecialization(nurse.getSpecialization());
                    detail.setBlood(nurse.getBloodGroup());
                    detail.setAddress(nurse.getAddress());
                    detail.setBirthDate(nurse.getBirthDate().toString());
                    detail.setProfileImage(nurse.getProfileImage());
                    detail.setHead(nurse.isHead());

                    // Add the nurse detail to the list
                    nurseDetails.add(detail);
                }
            }

            response.setResponse(nurseDetails);
        } catch (Exception e) {
            log.error("getOnShiftNurses | Exception occurred: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }


    public WardResponse getWardDetails(String userId) {
        log.info("getWardDetails | request received to view ward-details.");
        WardResponse response = new WardResponse();
        List<WardDetail> wardDetails = new ArrayList<>();

        try {
            if (userId == null || userId.isBlank()) {
                throw new RequestValidationException("Empty userId passed in the request");
            }

            Nurse nurse = nurseRepo.findById(userId).orElse(null);
            if (nurse == null) {
                response.setError("Nurse not found in the nurse table");
                return response;
            }

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            if (nurse.isHead() || ShiftUtility.isNurseOnShift(nurse, hour, currentDayOfWeek)) {

                List<Ward> wards = wardRepo.findAll();
                WardDetail detail;
                // Populate response based on the retrieved ward details
                for (Ward ward : wards) {
                    detail = new WardDetail();
                    detail.setPatientId(ward.getPatientId());
                    detail.setFirstName(ward.getFirstName());
                    detail.setLastName(ward.getLastName());
                    detail.setEmpty(ward.isEmpty());
                    detail.setWardNo(ward.getWardNo());
                    detail.setType(ward.getWardType());
                    detail.setDate(Utility.getFormattedOffsetTime(ward.getDate()));

                    log.info("Ward: "+detail);
                    wardDetails.add(detail);
                }

                // Set the ward details in the response
                response.setResponse(wardDetails);

            } else {
                log.info("getWardDetails | Unauthorized access by NURSE or NURSE is not on the shift.");
            }
            response.setResponse(wardDetails);


        } catch (RequestValidationException e) {
            log.error("getWardDetails | RequestValidationException occurred: " + e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("getWardDetails | Exception occurred: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    public GeneralResp updateWard(PatientDetail request, String userId) {
        log.info("updateWard | request received to update ward-detail");
        GeneralResp response = new GeneralResp();
        OffsetDateTime time = OffsetDateTime.now();
        try {
            if (userId == null || userId.isBlank()) {
                throw new RequestValidationException("Empty userId passed in the request");
            }

            // Check if patientId, wardNo, type, and action are present
            if (request == null || request.getAadhaar() == null || request.getAadhaar().isBlank() ||
                    request.getWardNo() == null || request.getWardNo().isBlank() ||
                    request.getAction() == null || request.getAction().isBlank()) {
                throw new RequestValidationException("All required fields must be provided.");
            }

            request.setAction(request.getAction().toUpperCase());

            if (!request.getAction().equals("A") &&
                    !request.getAction().equals("D")) {
                throw new RequestValidationException("Invalid action, action must be 'A' for allotment or 'D' for discharge.");
            }

            Nurse nur = nurseRepo.findById(userId).orElse(null);
            if (nur == null) {
                throw new RequestValidationException("Nurse not found in the table");
            }

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            if (nur.isHead() || ShiftUtility.isNurseOnShift(nur, hour, currentDayOfWeek)) {

                Optional<Ward> wardOptional = wardRepo.findByWardNo(request.getWardNo());
                if (wardOptional.isEmpty()) {
                    throw new RequestValidationException("WardNo not found in the table");
                }
                Ward ward = wardOptional.get();
                //In case of admit
                if ("A".equals(request.getAction())) {

                    if (!ward.isEmpty()) {
                        throw new RequestValidationException("Ward is already allocated to a patient");
                    }

                    Optional<Patient> optP = patientRepo.findById(request.getAadhaar());
                    if (optP.isEmpty()) {
                        throw new RequestValidationException("No such patient found in the patient-table");
                    }

                    Optional<Admit> optionalAdmit = admitRepo.findByPatientIdAndActive(request.getAadhaar(), true);
                    if (optionalAdmit.isEmpty()) {
                        throw new RequestValidationException("patient not found in the admit-table");
                    }

                    Admit admit = optionalAdmit.get();
                    admit.setPatientType("IP");
                    admit.setDate(time);

                    wardOptional = wardRepo.findByPatientId(request.getAadhaar());
                    if(wardOptional.isPresent()){
                        //If the patient is already attached to one wardID
                        Ward oldRow = wardOptional.get();

                        WardHistory wardHistory = new WardHistory();
                        wardHistory.setHistoryId(Utility.getUniqueId());
                        wardHistory.setWardNo(oldRow.getWardNo());
                        wardHistory.setPatientId(oldRow.getPatientId());
                        wardHistory.setDate(OffsetDateTime.now());
                        wardHistoryRepo.save(wardHistory);

                        oldRow.setPatientId(null);
                        oldRow.setFirstName(null);
                        oldRow.setLastName(null);
                        oldRow.setEmpty(true);
                        oldRow.setDate(time);
                        wardRepo.save(oldRow);
                    }

                    Patient p = optP.get();
                    p.setWardNo(ward.getWardNo());
                    p.setPatientType("IP");

                    ward.setPatientId(request.getAadhaar());
                    ward.setFirstName(p.getFirstName());
                    ward.setLastName(p.getLastName());
                    ward.setEmpty(false);

                    //Need to update the entry in table patient, admit, ward
                    admitRepo.save(admit);
                    Integer count = patientRepo.updatePatientRegistration(p.getAadhar(), "IP", ward.getWardNo(), time);
                    log.info("Count updated: "+count);
                    wardRepo.save(ward);

                } else {
                    //In case of discharge
                    if (ward.isEmpty()) {
                        log.info("updateWard | ward is already empty.");
                    }
                    else {
                        if (!request.getAadhaar().equals(ward.getPatientId())) {
                            throw new Exception("Patient is not allocated to this ward, check the request and table-data");
                        }

                        Optional<Patient> optP = patientRepo.findById(request.getAadhaar());
                        if (optP.isEmpty()) {
                            throw new RequestValidationException("No such patient found in the patient-table");
                        }
                        Patient p = optP.get();
                        p.setWardNo("");
                        patientRepo.save(p);

                        WardHistory wardHistory = new WardHistory();
                        wardHistory.setHistoryId(Utility.getUniqueId());
                        wardHistory.setWardNo(ward.getWardNo());
                        wardHistory.setPatientId(ward.getPatientId());
                        wardHistory.setDate(OffsetDateTime.now());
                        wardHistoryRepo.save(wardHistory);

                        ward.setPatientId(null);
                        ward.setFirstName(null);
                        ward.setLastName(null);
                        ward.setEmpty(true);
                        ward.setDate(OffsetDateTime.now());
                        wardRepo.save(ward);
                    }
                }

            } else {
                log.info("updateWard | Unauthorized access by NURSE or NURSE is not on the shift.");
                response.setResponse("FAILED");
                return response;
            }

            response.setResponse("SUCCESS");
            log.info("updateWard | request processed successfully");

        } catch (RequestValidationException e) {
            log.error("updateWard | RequestValidationException occurred: " + e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("updateWard | Exception occurred: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }


    public DashboardResponse getDashBoard(String userId) {
        DashboardResponse response = new DashboardResponse();
//        PersonalDetail detail;
        try {
            if (userId == null || userId.isBlank()) {
                throw new RequestValidationException("Empty nurseId passed in the request");
            }

            Optional<Login> l = loginRepo.findAccountByUserId(userId, "NURSE");
            if(l.isEmpty()){
                throw new NoSuchAccountException("Invalid userId passed in the request");
            }

            Optional<Nurse> optNurse = nurseRepo.findById(userId);
            if (optNurse.isEmpty()) {
                throw new NoSuchAccountException("No such nurse found in the table");
            } else {
                PersonalDetail detail = getDetailForNurse(optNurse.get());
                detail.setEmail(l.get().getUsername());
                response.setDetail(detail);

                Shift shift = getShiftForNurse(optNurse.get());
                response.setShift(shift);
            }

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            int hour = now.getHour();
            int currentDayOfWeek = now.getDayOfWeek().getValue();
            if (ShiftUtility.isNurseOnShift(optNurse.get(), hour, currentDayOfWeek)) {
                response.setOnDuty(1);
            }

            if (optNurse.get().isHead() || response.getOnDuty() == 1) {
                //If head-nurse is on duty, display IP/OP patient count
                int ipPatientCount = admitRepo.countAdmitByActiveIsTrueAndPatientType("IP");
                int opPatientCount = admitRepo.countAdmitByActiveIsTrueAndPatientType("OP");
                response.setIpPatient(ipPatientCount);
                response.setOpPatient(opPatientCount);
            }

            log.info("getDashBoard | request processed successfully.");
        } catch (NoSuchAccountException e) {
            log.error("NoSuchAccountException | Exception occurred: " + e.getMessage());
            response.setError(e.getMessage());
        } catch (RequestValidationException e) {
            log.error("RequestValidationException | Exception occurred: " + e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("getDashBoard | Exception occurred: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    private Shift getShiftForNurse(Nurse nurse) {
        Shift obj = new Shift();
        obj.setMon(nurse.getMon());
        obj.setTue(nurse.getTue());
        obj.setWed(nurse.getWed());
        obj.setThu(nurse.getThu());
        obj.setFri(nurse.getFri());
        obj.setSat(nurse.getSat());
        obj.setSun(nurse.getSun());
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

        obj.setActive(true);

        //obj.setProfileImage(fileService.loadUserImage(nurse.getProfileImage()));
        return obj;
    }

}