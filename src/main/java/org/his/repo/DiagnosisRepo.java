package org.his.repo;

import org.his.entity.Admit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepo extends JpaRepository<Admit, String> {
}
