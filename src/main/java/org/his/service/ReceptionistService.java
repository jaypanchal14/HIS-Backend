package org.his.service;

import org.his.bean.*;
import org.his.entity.user.Doctor;
import org.his.entity.user.Patient;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReceptionistService {

    private final DoctorRepo doctorRepo;

    @Autowired
    public ReceptionistService(DoctorRepo doctorRepo,PatientRepo patientRepo) {
        this.doctorRepo = doctorRepo;
        this.patientRepo= patientRepo;
    }
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public ReceptionDetailResp viewSchedule(String id, String role) {
        ReceptionDetailResp response = new ReceptionDetailResp();
        if (!role.equals("RECEPTIONIST")) {
            response.setError("Doesn't have access to check the details.");
            return response;
        }

        List<Doctor> doctors = doctorRepo.findAll();
        if (doctors.isEmpty()) {
            response.setError("No doctors available.");
            return response;
        }

        // Create a list to store doctor details
//        List<DoctorDetail> doctorDetails = new ArrayList<>();
//
//        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//        // Iterate through the list of doctors and map their details directly
//        for (Doctor doctor : doctors) {
//            DoctorDetail detail = new DoctorDetail();
//            detail.setFirstName(doctor.getFirstName());
//            detail.setLastName(doctor.getLastName());
//            detail.setEmail(doctor.getEmail());
//            detail.setPhone(doctor.getPhoneNumber());
//            detail.setGender(doctor.getGender());
//            detail.setBlood(doctor.getBloodGroup());
//            detail.setAddress(doctor.getAddress());
//            detail.setBirthDate(dateFormat.format(doctor.getBirthDate()));
//            detail.setProfileImage(doctor.getProfileImage());
//
//            // Add the doctor detail to the list
//            doctorDetails.add(detail);
//        }
//
//        // Set the list of doctor details in the response
//        response.setResponse(doctorDetails);
//
//        return response;
//    }

        List<PersonalDetail> personalDetails = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Iterate through the list of doctors and map their details directly
        for (Doctor doctor : doctors) {
            PersonalDetail detail = new PersonalDetail();
            detail.setFirstName(doctor.getFirstName());
            detail.setLastName(doctor.getLastName());
            detail.setEmail(doctor.getEmail());
            detail.setPhone(doctor.getPhoneNumber());
            detail.setGender(doctor.getGender());
            detail.setBlood(doctor.getBloodGroup());
            detail.setAddress(doctor.getAddress());
            detail.setBirthDate(doctor.getBirthDate());
            System.out.println(detail.getBirthDate());
            detail.setProfileImage(doctor.getProfileImage());

            // Add the doctor detail to the list
            personalDetails.add(detail);
        }

        // Set the list of doctor details in the response
        response.setResponse(personalDetails);

        return response;
    }

    @Autowired
    private PatientRepo patientRepo;

    public void savePatient(Patient patient) {
        patientRepo.save(patient);
    }
    public GeneralResp registerPatient(String receptionistId, PatientDetail patientDetail) {
        GeneralResp response = new GeneralResp();
        Patient patient = new Patient();

        // Check if all required fields are present
        if (patientDetail.getId()==null || patientDetail.getFirstName() == null || patientDetail.getLastName() == null ||
                patientDetail.getPhone() == null || patientDetail.getGender() == null ||
                patientDetail.getBlood() == null || patientDetail.getAddress() == null ||
                patientDetail.getBirthDate() == null || patientDetail.getImagePath() == null) {
            response.setResponse("FAILED");
            response.setError("All required fields must be provided.");
            return response;
        }
        // Set patient details
        patient.setId(patientDetail.getId());
        patient.setFirstName(patientDetail.getFirstName());
        patient.setLastName(patientDetail.getLastName());
        patient.setEmail(patientDetail.getEmail());
        patient.setGender(patientDetail.getGender());
        patient.setBirthDate(Date.valueOf(dateFormat.format(patientDetail.getBirthDate())));
        patient.setPhoneNumber(patientDetail.getPhone());
        patient.setBloodGroup(patientDetail.getBlood());
        patient.setAddress(patientDetail.getAddress());
        patient.setProfileImage(patientDetail.getImagePath());
        patient.setPatientType("OP");

        try {
            patientRepo.save(patient);
            response.setResponse("SUCCESS");
        } catch (Exception e) {
            response.setResponse("FAILED");
            response.setError("Failed to register patient. Please try again.");
        }

        return response;
    }

    public PatientResponse isPatientPresent(PatientDetail patientDetail) {
        PatientResponse response = new PatientResponse();

        // Check if all required fields are present
        if (patientDetail.getId() == null &&
                (patientDetail.getFirstName() == null || patientDetail.getLastName() == null)) {
            response.setError("Either patientId or patient's first and last name must be provided.");
            return response;
        }
        List<PatientDetail> ls = new ArrayList<>();
        if (patientDetail.getId() != null) {
            // Search patient by patientId
            Patient patient = patientRepo.findById(patientDetail.getId()).orElse(null);
            if (patient != null) {
                ls.add(getPatientDetailsFromPatient(patient));
                response.setResponse(ls);
            } else {
                response.setError("Patient not found.");
            }
        } else {
            // Search patient by first and last name
            List<Patient> patients = patientRepo.findByFirstNameAndLastName(patientDetail.getFirstName(), patientDetail.getLastName());
            if (!patients.isEmpty()) {
                for(Patient p : patients){
                    ls.add(getPatientDetailsFromPatient(p));
                }
                response.setResponse(ls); // Assuming only one patient with the same first and last name
            } else {
                response.setError("Patient not found.");
            }
        }

        return response;
    }

    private PatientDetail getPatientDetailsFromPatient(Patient patient) {
        PatientDetail detail = new PatientDetail();
        detail.setFirstName(patient.getFirstName());
        detail.setLastName(patient.getLastName());
        detail.setGender(patient.getGender());
        detail.setId(patient.getId());
        detail.setPhone(patient.getPhoneNumber());
        detail.setImagePath(patient.getProfileImage());
        return detail;
    }
}
