package com.javeriana.component.repository;

import com.javeriana.component.model.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @Query("SELECT c.courseId FROM CourseEntity c WHERE c.courseId IN :courseids")
    List<String> findExistingCourseIds(@Param("courseids")List<String> courseIds);

    @Query("SELECT c.moodleId FROM CourseEntity c WHERE c.courseId = :courseid")
    String findMoodleIdByCourseId(@Param("courseid")String courseId);

    @Query("SELECT c.courseId FROM CourseEntity c WHERE c.moodleId = :moodleid")
    String findCourseIdByMoodleId(@Param("moodleid")String moodleid);


    @Modifying
    @Transactional
    @Query("UPDATE CourseEntity c SET c.moodleId = :id WHERE c.shortName = :shortname")
    int updateMoodleIdByShortName(@Param("id") Integer id, @Param("shortname") String shortName);

    @Modifying
    @Transactional
    @Query("UPDATE CourseEntity c SET c.sync = True WHERE c.courseId = :id")
    int updateSync(@Param("id") String id);


}
