package org.his.repo;

import org.his.entity.Admit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdmitRepo extends JpaRepository<Admit, String> {

    Optional<Admit> findByPatientId(String patientId);

    Optional<Admit> findByPatientIdAndActiveIsTrue(String patientId);

    List<Admit> findAllByActiveAndPatientType(boolean active, String patientType);

    Integer countAdmitByActiveIsTrueAndPatientType(String patientType);
}
