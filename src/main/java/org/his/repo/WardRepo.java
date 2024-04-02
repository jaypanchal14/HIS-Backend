package org.his.repo;

import org.his.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WardRepo extends JpaRepository<Ward, String> {
    Optional<Ward> findByWardNo(String wardNo);
}
