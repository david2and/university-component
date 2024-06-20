package com.javeriana.component.controller;

import com.javeriana.component.model.request.AdminRequest;
import com.javeriana.component.model.response.CoursesResponse;
import com.javeriana.component.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("admin")
public class UniversityController {

    @Autowired
    UniversityService universityService;

    @PutMapping("/grades")
    public void updateSpecificGrades(@Valid @RequestBody Optional<List<CoursesResponse>> coursesToUpdate) {
        if(coursesToUpdate.isEmpty()){
           universityService.syncCourses();
        }else{
            universityService.syncSpecificCourses(coursesToUpdate.get());
        }

    }

    @PostMapping("/login")
    public Boolean loginuser(@Valid @RequestBody AdminRequest adminRequest) {
        return universityService.validateUser(adminRequest);
    }

    @PostMapping("/registrar")
    public void registerUser(@Valid @RequestBody AdminRequest adminRequest) {
        universityService.registerUser(adminRequest);
    }

}
