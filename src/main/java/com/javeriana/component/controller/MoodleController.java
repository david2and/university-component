package com.javeriana.component.controller;

import com.javeriana.component.model.dto.GradeDTO;
import com.javeriana.component.model.dto.GradesResponseDTO;
import com.javeriana.component.model.response.CoursesResponse;
import com.javeriana.component.model.response.StudentGradesResponse;
import com.javeriana.component.service.MoodleService;
import com.javeriana.component.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("moodle")
public class MoodleController {

    @Autowired
    private MoodleService moodleService;

    @Autowired
    private UniversityService universityService;

    @GetMapping("/courses")
    public List<CoursesResponse> getCourses() {
        return moodleService.getCourses();
    }


    @GetMapping("/grades")
    public StudentGradesResponse getGrades() {
        return moodleService.getGrades();
    }

}