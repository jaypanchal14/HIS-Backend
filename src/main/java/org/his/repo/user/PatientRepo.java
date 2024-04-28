package org.his.repo.user;

import org.his.entity.user.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface PatientRepo extends JpaRepository<Patient, String> {

    List<Patient> findByFirstNameAndLastName(String firstName, String lastName);

    @Transactional
    @Modifying
    @Query("UPDATE Patient p set p.patientType = ?2,p.wardNo = ?3, p.updatedAt = ?4  where p.aadhar = ?1")
    Integer updatePatientRegistration(String aadhaar, String type, String ward, OffsetDateTime time);

    List<Patient> findAllByAadharIn(Set<String> aadhar);

}