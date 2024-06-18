package com.javeriana.component.service;

import com.javeriana.component.model.entity.CourseEntity;
import com.javeriana.component.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public void saveCourse(CourseEntity courseEntity){
        courseRepository.save(courseEntity);
    }

    public void saveCourses(List<CourseEntity> courses){
        courseRepository.saveAll(courses);
    }

    public List<String> findExistingCourseIdIn(List<String> courseIds){
        return courseRepository.findExistingCourseIds(courseIds);
    }

    public void updateMoodleIdByShortName(Integer moodleId, String shortName){
        courseRepository.updateMoodleIdByShortName(moodleId,shortName);
    }

    public String getMoodleIdByCourseId(String courseId){
        return courseRepository.findMoodleIdByCourseId(courseId);
    }

    public String getCourseIdByMoodleId(String moodleId){
        return courseRepository.findCourseIdByMoodleId(moodleId);
    }
}
