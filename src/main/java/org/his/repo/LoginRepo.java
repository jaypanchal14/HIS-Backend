package org.his.repo;

import org.his.bean.RoleCount;
import org.his.bean.ViewUserIdentifier;
import org.his.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface LoginRepo extends JpaRepository<Login, String> {


    @Query("SELECT l FROM Login l where l.username = ?1 and l.isActive = true")
    Optional<Login> findByUserDetail(String username);

    //Below method is equivalent to findById
    @Query("SELECT l FROM Login l where l.username = ?1 and l.role = ?2")
    Optional<Login> findAccountByUsername(String username, String role);

    @Query("SELECT l FROM Login l where l.userId = ?1 and l.role = ?2")
    Optional<Login> findAccountByUserId(String userId, String role);

    @Transactional
    @Modifying
    @Query("UPDATE Login l set l.password = ?2 where l.userId = ?1")
    Integer updatePassword(String userId, String password);

    @Transactional
    @Modifying
    @Query("UPDATE Login l set l.isActive = ?2 where l.userId = ?1")
    Integer updateAccountStatus(String userId, boolean isActive);

    @Query("SELECT l.userId FROM Login l where l.username = ?1 and l.role = ?2")
    Optional<String> findUserIdByUsername(String username, String role);

    @Query("SELECT new org.his.bean.RoleCount( l.role ,COUNT(l)) FROM Login l WHERE l.isActive = true GROUP BY l.role")
    List<RoleCount> countActiveUserByRole();

    @Query("SELECT l FROM Login l WHERE l.userId = ?1 and l.role = ?2 and l.isActive = true")
    Optional<Login> checkIfUserIsActive(String userId, String role);

    @Query("SELECT new org.his.bean.ViewUserIdentifier(l.role, l.userId, l.username) FROM Login l WHERE l.role!='ADMIN' and l.isActive = true")
    List<ViewUserIdentifier> getActiveUsers();

    @Query("SELECT new org.his.bean.ViewUserIdentifier(l.role, l.userId, l.username) FROM Login l WHERE l.role = ?1 and l.isActive = true")
    List<ViewUserIdentifier> getActiveUsersBasedOnRole(String role);

    Optional<Login> findFirstByIsActiveIsTrueAndRole(String role);

}