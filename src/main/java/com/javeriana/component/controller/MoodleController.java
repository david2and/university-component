package com.javeriana.component.controller;

import com.javeriana.component.model.request.CoursesSaveRequest;
import com.javeriana.component.model.request.RegistersSaveRequest;
import com.javeriana.component.model.request.UsersSaveRequest;
import com.javeriana.component.model.response.*;
import com.javeriana.component.service.MoodleService;
import com.javeriana.component.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping("/users")
    public ResponseEntity<List<UsersSaveResponse>> saveUsers(@Valid @RequestBody List<UsersSaveRequest> usersSaveRequest) {
        List<UsersSaveResponse> response = moodleService.saveUsers(usersSaveRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/courses")
    public ResponseEntity<List<CoursesSaveResponse>> saveCourses(@Valid @RequestBody List<CoursesSaveRequest> coursesSaveRequest) {
        List<CoursesSaveResponse> response = moodleService.saveCourses(coursesSaveRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/registers")
    public ResponseEntity<String> saveRegisters(@Valid @RequestBody List<RegistersSaveRequest> registersSaveRequest) {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}