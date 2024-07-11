package com.javeriana.component.repository;

import com.javeriana.component.model.entity.RegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistersRepository extends JpaRepository<RegisterEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RegisterEntity r WHERE r.userName = :username AND r.courseId = :courseid")
    boolean  findExistingUserNames(@Param("username")String username, @Param("courseid")String courseid);

    void deleteByUserNameAndCourseId(String userName, String courseId);

}