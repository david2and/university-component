package com.javeriana.component.repository;

import com.javeriana.component.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u.userName FROM UserEntity u WHERE u.userName IN :usernames")
    List<String> findExistingUserNames(@Param("usernames")List<String> usernames);

    @Query("SELECT u.moodleId FROM UserEntity u WHERE u.userName IN :username")
    String findMoodleIdByUserName(@Param("username")String username);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.moodleId = :id WHERE u.userName = :username")
    void updateMoodleIdByUsername(@Param("id") Integer id, @Param("username") String userName);
}
