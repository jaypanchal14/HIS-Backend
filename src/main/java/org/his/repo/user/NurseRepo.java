package org.his.repo.user;

import org.his.entity.user.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseRepo extends JpaRepository<Nurse, String> {

}