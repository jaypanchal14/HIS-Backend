package org.his.repo;

import org.his.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WardRepo extends JpaRepository<Ward, String> {
}
