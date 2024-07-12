package com.javeriana.component.controller;

import com.javeriana.component.model.request.AdminRequest;
import com.javeriana.component.model.response.CoursesResponse;
import com.javeriana.component.service.JwtService;
import com.javeriana.component.service.UniversityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("admin")
public class UniversityController {

    @Autowired
    UniversityService universityService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;


    @Tag(name = "Sync Grades", description = "Sync Grades with the University Endpoint")
    @PutMapping("/grades")
    public void updateSpecificGrades(@Valid @RequestBody Optional<List<CoursesResponse>> coursesToUpdate) {
        if(coursesToUpdate.isEmpty()){
           universityService.syncCourses();
        }else{
            universityService.syncSpecificCourses(coursesToUpdate.get());
        }

    }

    @Tag(name = "Login", description = "User user and password to get auth token")
    @PostMapping("/login")
    public String loginuser(@Valid @RequestBody AdminRequest adminRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(adminRequest.getUsername(), adminRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(adminRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request ");
        }
    }

    @Tag(name = "Register", description = "Register a new user")
    @PostMapping("/registrar")
    public void registerUser(@Valid @RequestBody AdminRequest adminRequest) {
        universityService.registerUser(adminRequest);
    }

}
