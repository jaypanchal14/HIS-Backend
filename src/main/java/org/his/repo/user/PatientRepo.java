package org.his.repo.user;

import org.his.bean.PatientDetail;
import org.his.entity.user.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepo extends JpaRepository<Patient, String> {
    List<Patient> findByFirstNameAndLastName(String firstName, String lastName);

}