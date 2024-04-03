package org.his.repo.user;

import org.his.entity.user.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, String> {

    List<Doctor> findAllByIdIn(List<String> id);

}