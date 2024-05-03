package org.his.repo;

import org.his.entity.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRepo extends JpaRepository<Emergency, String> {

    List<Emergency> findAllByHandledIsFalseOrderByDateDesc();
}