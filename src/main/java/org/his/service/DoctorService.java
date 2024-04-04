package org.his.service;
import org.his.bean.PatientDetail;
import org.his.bean.PatientResponse;
import org.his.entity.user.Patient;
import org.his.repo.user.DoctorRepo;
import org.his.repo.user.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepo doctorRepo;
    private final PatientRepo patientRepo;

    @Autowired
    public DoctorService(DoctorRepo doctorRepo, PatientRepo patientRepo) {

        this.doctorRepo= doctorRepo;
        this.patientRepo = patientRepo;
    }


    public PatientResponse viewPastPatients() {
        PatientResponse response = new PatientResponse();


        // Check if the role is supported
//        if (!role.equalsIgnoreCase("DOCTOR")) {
//            response.setError("Role not supported.");
//            return response;
//        }

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
                detail.setId(patient.getId());
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


}
