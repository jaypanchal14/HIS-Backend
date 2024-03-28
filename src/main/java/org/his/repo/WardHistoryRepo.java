package org.his.repo;

import org.his.entity.WardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WardHistoryRepo extends JpaRepository<WardHistory, String> {
}
