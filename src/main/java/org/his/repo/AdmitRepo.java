package org.his.repo;

import org.his.entity.Admit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmitRepo extends JpaRepository<Admit, String> {

    Optional<Admit> findByPatientId(String patientId);

    Optional<Admit> findByPatientIdAndActiveIsTrue(String patientId);

    List<Admit> findAllByActiveAndPatientType(boolean active, String patientType);

    Integer countAdmitByActiveIsTrueAndPatientType(String patientType);

    Optional<Admit> findByAdmitIdAndActiveIsTrue(String admitId);

    @Transactional
    @Modifying
    @Query("UPDATE Admit a set a.active = ?2 where a.admitId = ?1")
    Integer updateAdmitStatus(String admitId, boolean status);

}