package org.his.repo.user;

import org.his.entity.user.Pharma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmaRepo extends JpaRepository<Pharma, String> {

}