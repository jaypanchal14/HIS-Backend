package org.his.repo.user;

import org.his.entity.user.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepo extends JpaRepository<Receptionist, String> {

}