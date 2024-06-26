package org.his.repo;

import org.his.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepo extends JpaRepository<Diagnosis, String> {

    List<Diagnosis> findAllByAdmitIdOrderByDateDesc(String admitId);

    Optional<Diagnosis> findByDiagnosisIdAndFile(String diagnosisId, String file);

    @Query("select distinct d.admitId from Diagnosis d where d.role= ?1 and d.userId = ?2")
    List<String> findDistinctAdmitIdByUserId(String role,String userId);

}
