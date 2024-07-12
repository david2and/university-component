package com.javeriana.component.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.javeriana.component.model.response.CoursesResponse;
import com.javeriana.component.model.response.StudentGradesResponse;
import com.javeriana.component.service.MoodleService;
import com.javeriana.component.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("moodle")
public class MoodleController {

    @Autowired
    private MoodleService moodleService;

    @Autowired
    private UniversityService universityService;

    @Tag(name = "Get Courses", description = "Retrieves all the courses")
    @GetMapping("/courses")
    public List<CoursesResponse> getCourses(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer token")
            @RequestHeader("Authorization") String authorizationHeader) {
        return moodleService.getCourses();
    }


    @Tag(name = "Get Grades", description = "Retrieves all the grades by courses")
    @GetMapping("/grades")
    public StudentGradesResponse getGrades(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer token")
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return moodleService.getGrades();
    }

}