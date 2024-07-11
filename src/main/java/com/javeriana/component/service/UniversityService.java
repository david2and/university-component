package com.javeriana.component.service;


import com.javeriana.component.config.UniversityEndpointsConfig;
import com.javeriana.component.config.UrlConfig;
import com.javeriana.component.model.entity.*;
import com.javeriana.component.model.request.AdminRequest;
import com.javeriana.component.model.request.CoursesRequest;
import com.javeriana.component.model.request.GradesRequest;
import com.javeriana.component.model.request.SyncRequest;
import com.javeriana.component.model.response.CoursesResponse;
import com.javeriana.component.model.response.StudentGradesResponse;
import com.javeriana.component.rest.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class UniversityService {



    @Autowired
    UrlConfig urlConfig;

    @Autowired
    UniversityEndpointsConfig universityEndpointsConfig;

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @Autowired
    MoodleService moodleService;

    @Autowired
    AdminService adminService;

    @Autowired
    private RestClient restClient;


    public void syncCourses(){
        List<CoursesResponse> courses = moodleService.getCourses();
        StudentGradesResponse studentGradesResponse = moodleService.getGrades();
        Map<String, String> params = new HashMap<>();

        SyncRequest syncRequest = new SyncRequest();
        List<CoursesRequest> coursesRequests = courses.stream().map(coursesResponse -> {
            CoursesRequest coursesRequest = new CoursesRequest();
            coursesRequest.setCourseId(coursesResponse.getIdCourse());
            coursesRequest.setName(coursesRequest.getName());
            coursesRequest.setArea(coursesResponse.getArea());

            List<GradesRequest> gradesRequests = studentGradesResponse.getStudents().stream().map(studentResponse -> {
                GradesRequest gradesRequest = new GradesRequest();
                gradesRequest.setStudentUserName(studentResponse.getUsername());
                gradesRequest.setGrades(studentResponse.getGrades());
                return gradesRequest;
            }).toList();
            coursesRequest.setGradesRequests(gradesRequests);
            return coursesRequest;
        }).toList();

        syncRequest.setCoursesRequests(coursesRequests);

        restClient.postGrades(urlConfig.getUniversityUrl()+universityEndpointsConfig.getSyncGrades(),params, syncRequest);

        courses.forEach(coursesResponse -> {
            courseService.updateSync(coursesResponse.getIdCourse());
        });

    }

    public void syncSpecificCourses(List<CoursesResponse> coursesToUpdate){
        StudentGradesResponse studentGradesResponse = moodleService.getGrades();
        Map<String, String> params = new HashMap<>();

        SyncRequest syncRequest = new SyncRequest();
        List<CoursesRequest> coursesRequests = coursesToUpdate.stream().map(coursesResponse -> {
            CoursesRequest coursesRequest = new CoursesRequest();
            coursesRequest.setCourseId(coursesResponse.getIdCourse());
            coursesRequest.setName(coursesRequest.getName());
            coursesRequest.setArea(coursesResponse.getArea());

            List<GradesRequest> gradesRequests = studentGradesResponse.getStudents().stream().map(studentResponse -> {
                GradesRequest gradesRequest = new GradesRequest();
                gradesRequest.setStudentUserName(studentResponse.getUsername());
                gradesRequest.setGrades(studentResponse.getGrades());
                return gradesRequest;
            }).toList();
            coursesRequest.setGradesRequests(gradesRequests);
            return coursesRequest;
        }).toList();

        syncRequest.setCoursesRequests(coursesRequests);

        restClient.postGrades(urlConfig.getUniversityUrl()+universityEndpointsConfig.getSyncGrades(),params, syncRequest);

        coursesToUpdate.forEach(coursesResponse -> {
            courseService.updateSync(coursesResponse.getIdCourse());
        });

    }

    public void registerUser(AdminRequest adminRequest){
        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setUserName(adminRequest.getUsername());
        adminEntity.setPassword(adminRequest.getPassword());

        adminService.saveAdminUser(adminEntity);
    }

    public Boolean validateUser(AdminRequest adminRequest){
        return adminService.validateAdminUser(adminRequest.getUsername(),adminRequest.getPassword())!=null;
    }




}