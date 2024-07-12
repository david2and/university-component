package com.javeriana.component.repository;

import com.javeriana.component.model.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {

    @Query("SELECT u.userName FROM AdminEntity u WHERE u.userName = :username AND u.password = :password")
    String validateUser(@Param("username")String username,@Param("password")String password);

    Optional<AdminEntity> findByUserName(String username);

}



