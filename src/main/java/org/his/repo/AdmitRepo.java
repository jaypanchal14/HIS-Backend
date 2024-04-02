package org.his.repo;

import org.his.entity.Admit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdmitRepo extends JpaRepository<Admit, String> {
    Optional<Admit> findByPatientId(String patientId);
}
