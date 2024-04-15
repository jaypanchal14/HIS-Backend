package org.his.repo;

import org.his.entity.WardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardHistoryRepo extends JpaRepository<WardHistory, String> {
}
