package org.his.service;
import org.his.bean.*;
import org.his.entity.Ward;
import org.his.entity.user.Nurse;
import org.his.entity.user.Patient;
import org.his.repo.WardRepo;
import org.his.repo.user.NurseRepo;
import org.his.repo.user.PatientRepo;
import org.his.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NurseService {

    private final NurseRepo nurseRepo;
    private final WardRepo wardRepo;

    //@Autowired
    private PatientRepo patientRepo;

    @Autowired
    public NurseService(NurseRepo nurseRepo, WardRepo wardRepo, PatientRepo patientRepo) {

        this.nurseRepo = nurseRepo;
        this.wardRepo = wardRepo;
        this.patientRepo = patientRepo;
    }

    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public ReceptionDetailResp getOnShiftNurses(String id, String role) {
        ReceptionDetailResp response = new ReceptionDetailResp();

        if (!role.equals("NURSE")) {
            response.setError("Nurse doesn't have privilege to see or any other reason");
            return response;
        }
        Nurse nur = nurseRepo.findById(id).orElse(null);
        System.out.println(nur.isHead());
        if (!nur.isHead()) {
            response.setError("Nurse doesn't have privilege to see or any other reason");
            return response;
        }
        List<Nurse> nurses = new ArrayList<>();
        // Check if the nurse is head nurse
        Nurse headNurse = nurseRepo.findById(id).orElse(null);
        if (headNurse != null && headNurse.isHead()) {
            nurses.addAll(nurseRepo.findAll());
        }

        if (nurses.isEmpty()) {
            response.setError("No nurses available.");
            return response;
        }

        // Create a list to store nurse details
        List<PersonalDetail> nurseDetails = new ArrayList<>();

        // Iterate through the list of nurses and map their details
        for (Nurse nurse : nurses) {
            if (!nurse.isHead()) {
                PersonalDetail detail = new PersonalDetail();
                detail.setFirstName(nurse.getFirstName());
                detail.setLastName(nurse.getLastName());
                //Removing email from the nurse table
                //detail.setEmail(nurse.getEmail());
                detail.setPhone(nurse.getPhoneNumber());
                detail.setGender(nurse.getGender());
                detail.setSpecialization(nurse.getSpecialization());
                detail.setBlood(nurse.getBloodGroup());
                detail.setAddress(nurse.getAddress());
                detail.setBirthDate(nurse.getBirthDate().toString());
                detail.setProfileImage(nurse.getProfileImage());
                detail.setHead(nurse.isHead());
                System.out.println(nurse.isHead());

                // Add the nurse detail to the list
                nurseDetails.add(detail);
            }
        }

        // Set the list of nurse details in the response
        response.setResponse(nurseDetails);

        return response;
    }


    public WardResponse getWardDetails(String id, String role) {
        WardResponse response = new WardResponse();
        if (!isValidNurse(id)) {
            response.setError("Invalid nurse credentials or unauthorized access.");
            return response;
        }
        // Check if the nurse is authorized to view ward details
        if (!role.equals("NURSE")) {
            response.setError("Nurse role required to view ward details.");
            return response;
        }

//         Retrieve ward details based on whether the ward is empty or not
        List<Ward> wards = new ArrayList<>();
        if (role.equals("NURSE") && !id.isEmpty()) {
            wards.addAll(wardRepo.findAll());
        } else {
            wards = wardRepo.findAll();
        }

        // Populate response based on the retrieved ward details
        List<WardDetail> wardDetails = new ArrayList<>();
        for (Ward ward : wards) {
            WardDetail detail = new WardDetail();
            detail.setWardNo(ward.getWardNo());
            detail.setType(ward.getWardType());
            detail.setDate(Utility.getFormattedOffsetTime(ward.getDate()));
            detail.setEmpty(ward.isEmpty());
            detail.setPatientId(ward.getPatientId());
            detail.setFirstName(ward.getFirstName());
            detail.setLastName(ward.getLastName());
            wardDetails.add(detail);
        }

        // Set the ward details in the response
        response.setResponse(wardDetails);
        return response;
    }

        private boolean isValidNurse(String nurseId) {
            Nurse nurse = nurseRepo.findById(nurseId).orElse(null);
            if(nurse == null) {
                return false;  // Nurse ID not found
            }
            return true;
        }



    public GeneralResp updateWard(PatientDetail patientDetail, String nurseId) {
        GeneralResp response = new GeneralResp();

        Nurse nur=nurseRepo.findById(nurseId).orElse(null);
           if(nur==null)
           {
               response.setError("No access for nurse");
               return response;
           }
        // Check if patientId, wardNo, type, and action are present
        if (patientDetail == null || patientDetail.getId() == null ||
                patientDetail.getWardNo() == null ||
                patientDetail.getAction() == null) {
            response.setError("All required fields must be provided.");
            return response;
        }

        if (!patientDetail.getAction().equalsIgnoreCase("A") &&
                !patientDetail.getAction().equalsIgnoreCase("D")) {
            response.setError("Invalid action. Action must be 'A' for allotment or 'D' for discharge.");
            return response;
        }

        Optional<Ward> wardOptional = wardRepo.findByWardNo(patientDetail.getWardNo());
        if (wardOptional.isEmpty()) {
            response.setError("Ward not found.");
            return response;
        }
        Ward ward = wardOptional.get();

        Optional<Patient> optP = patientRepo.findById(patientDetail.getId());
        if(optP.isEmpty()){
            response.setError("No such patient found.");
            return response;
        }
        Patient p = optP.get();

        if (patientDetail.getAction().equalsIgnoreCase("A")) {
            ward.setPatientId(patientDetail.getId());
            ward.setFirstName(p.getFirstName());
            ward.setLastName(p.getLastName());
            ward.setEmpty(false);
        } else {
            if (ward.getPatientId() == null || !ward.getPatientId().equals(patientDetail.getId())) {
                response.setError("Patient is not allocated to this ward.");
                return response;
            }
            ward.setPatientId(null);
            ward.setFirstName(null);
            ward.setLastName(null);
            ward.setEmpty(true);
        }
        wardRepo.save(ward);

        response.setResponse("SUCCESS");
        return response;
    }


}

