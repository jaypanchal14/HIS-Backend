package org.his.repo;

import org.his.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepo extends JpaRepository<Ward, String> {
    Optional<Ward> findByWardNo(String wardNo);

    Optional<Ward> findByPatientId(String patientId);
}
