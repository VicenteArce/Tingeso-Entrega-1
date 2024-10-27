package com.Tingeso.prestabanco.Repositories;

import com.Tingeso.prestabanco.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u.rol FROM UserEntity u WHERE u.userId = :userId")
    Integer findRolByUserId(@Param("userId") Long userId);
    UserEntity findByRut(String rut);
    UserEntity findByEmail(String email);
}
