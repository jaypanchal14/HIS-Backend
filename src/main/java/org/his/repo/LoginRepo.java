package org.his.repo;

import org.his.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoginRepo extends JpaRepository<Login, String> {

    @Query("SELECT l FROM Login l where l.username = ?1")
    public Optional<Login> findAccountByUsername(String username);

}