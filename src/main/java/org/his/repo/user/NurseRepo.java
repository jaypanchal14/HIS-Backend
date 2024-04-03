package org.his.repo.user;

import org.his.entity.user.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NurseRepo extends JpaRepository<Nurse, String> {

    List<Nurse> findAllByIdIn(List<String> id);

}