package com.javeriana.component.service;

import com.javeriana.component.config.MoodleFunctionsEnum;
import com.javeriana.component.config.UrlConfig;
import com.javeriana.component.model.dto.*;
import com.javeriana.component.model.entity.CourseEntity;
import com.javeriana.component.model.entity.UserEntity;
import com.javeriana.component.model.response.*;
import com.javeriana.component.rest.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MoodleService {

    @Autowired
    UrlConfig urlConfig;

    @Autowired
    private RestClient restClient;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;


    public StudentGradesResponse getGrades() {

        List<UserEntity> userEntities = userService.findAll();
        StudentGradesResponse studentGradesResponse = new StudentGradesResponse();

        List<StudentResponse> studentResponses = userEntities.stream().map(userEntity -> {
            Map<String, String> paramsMoodle = new HashMap<>();
            paramsMoodle.put("userid",userEntity.getMoodleId().toString());
            GradesResponseDTO gradesResponseDTOS = (GradesResponseDTO) restClient.getApiDataWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.GRADE_REPORT.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<GradesResponseDTO>() {});

            StudentResponse studentResponse = new StudentResponse();


            List<Grades> grades = gradesResponseDTOS.getGrades().stream().map(gradesResponseDTO -> {
                Grades gradesResponse = new Grades();
                gradesResponse.setCourseId(courseService.getCourseIdByMoodleId(gradesResponseDTO.getCourseid().toString()));
                gradesResponse.setGrade(gradesResponseDTO.getGrade());
                gradesResponse.setRawGrade(gradesResponseDTO.getRawgrade());
                return gradesResponse;

            }).collect(Collectors.toList());
            studentResponse.setGrades(grades);
            studentResponse.setUsername(userEntity.getUserName());
            return studentResponse;

        }).collect(Collectors.toList());
        studentGradesResponse.setStudents(studentResponses);

        return studentGradesResponse;

    }

    public List<CoursesResponse> getCourses(){
        List<CourseEntity> courseEntities = courseService.findAllCourses();
        return courseEntities.stream().map(courseEntity ->{
                    CoursesResponse coursesResponse = new CoursesResponse();
                    coursesResponse.setIdCourse(courseEntity.getCourseId());
                    coursesResponse.setName(courseEntity.getFullName());
                    coursesResponse.setArea(courseEntity.getCategoryName());
                    coursesResponse.setSync(courseEntity.getSync());
                    return coursesResponse;
                }).toList();
    }


}
