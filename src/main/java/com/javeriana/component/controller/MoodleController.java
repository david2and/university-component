package com.javeriana.component.controller;

import com.javeriana.component.errors.WrongFormatFieldException;
import com.javeriana.component.model.request.CoursesSaveRequest;
import com.javeriana.component.model.request.RegistersSaveRequest;
import com.javeriana.component.model.request.UsersSaveRequest;
import com.javeriana.component.model.response.*;
import com.javeriana.component.service.MoodleService;
import com.javeriana.component.service.UniversityService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("moodle")
public class MoodleController {

    @Autowired
    private MoodleService moodleService;

    @Autowired
    private UniversityService universityService;

    @Tag(name = "Get Courses", description = "Retrieves all the courses")
    @GetMapping("/courses")
    public List<CoursesResponse> getCourses() {
        return moodleService.getCourses();
    }

    @Tag(name = "Get Grades", description = "Retrieves all the grades by courses")
    @GetMapping("/grades")
    public StudentGradesResponse getGrades() {
        return moodleService.getGrades();
    }

    @Tag(name = "Save Users", description = "Save users in Moodle")
    @PostMapping("/users")
    public ResponseEntity<List<UsersSaveResponse>> saveUsers(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer token")
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid List<UsersSaveRequest> usersSaveRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new WrongFormatFieldException(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(" - ", "{ ", " }")));
        }
        List<UsersSaveResponse> response = moodleService.saveUsers(usersSaveRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "Save Courses", description = "Save courses in Moodle")
    @PostMapping("/courses")
    public ResponseEntity<List<CoursesSaveResponse>> saveCourses(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer token")
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody List<CoursesSaveRequest> coursesSaveRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new WrongFormatFieldException(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(" - ", "{ ", " }")));
        }
        List<CoursesSaveResponse> response = moodleService.saveCourses(coursesSaveRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "Registers", description = "Register users in the requested course")
    @PostMapping("/registers")
    public ResponseEntity<String> saveRegisters(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer token")
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody List<RegistersSaveRequest> registersSaveRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new WrongFormatFieldException(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(" - ", "{ ", " }")));
        }
        moodleService.saveRegisters(registersSaveRequest);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}