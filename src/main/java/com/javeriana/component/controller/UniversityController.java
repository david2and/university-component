package com.javeriana.component.controller;

import com.javeriana.component.model.response.StudentGradesResponse;
import com.javeriana.component.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UniversityController {

    @Autowired
    UniversityService universityService;

    @GetMapping("/syncGrades")
    public void getGrades() {
         universityService.syncCourses();
    }

}
