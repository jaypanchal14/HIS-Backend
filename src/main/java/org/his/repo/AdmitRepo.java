package org.his.repo;

import org.his.entity.Admit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmitRepo extends JpaRepository<Admit, String> {
}
