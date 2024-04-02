package org.his.repo;

import org.his.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepo extends JpaRepository<Prescription, String> {

    List<Prescription> findByDateBetween(OffsetDateTime start, OffsetDateTime end);

    List<Prescription> findByDateAfter(OffsetDateTime date);

    Optional<Prescription> findByDiagnosisId(String diagnosisId);

}
