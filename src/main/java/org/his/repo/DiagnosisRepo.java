package org.his.repo;

import org.his.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepo extends JpaRepository<Diagnosis, String> {

    List<Diagnosis> findAllByAdmitIdOrderByDateDesc(String admitId);

}
