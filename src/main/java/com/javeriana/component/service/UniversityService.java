package com.javeriana.component.service;

import com.javeriana.component.config.MoodleFunctionsEnum;
import com.javeriana.component.config.TaskSchedulingConfig;
import com.javeriana.component.config.UniversityEndpointsConfig;
import com.javeriana.component.config.UrlConfig;
import com.javeriana.component.model.MoodleRolesEnum;
import com.javeriana.component.model.dto.*;
import com.javeriana.component.model.entity.CourseEntity;
import com.javeriana.component.model.entity.RegisterEntity;
import com.javeriana.component.model.entity.UserEntity;
import com.javeriana.component.rest.RestClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

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
    RegisterService registerService;

    @Autowired
    private TaskSchedulingConfig taskSchedulingConfig;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(taskSchedulingConfig.getZone()));
    }

    @Autowired
    private RestClient restClient;

    @Transactional
    @Scheduled(cron = "#{@taskSchedulingConfig.getCron()}", zone = "#{@taskSchedulingConfig.getZone()}")
    public void sheduleUpdateFunction(){
        Map<String, String> params = new HashMap<>();

        List<UserDTO> universityUsers = restClient.getApiDataListWithDynamicParams(urlConfig.getUniversityUrl()+universityEndpointsConfig.getUsersEndpoint(),params, new ParameterizedTypeReference<List<UserDTO>>() {});
        List<String> universityUserNames = universityUsers.stream().map(userDTO -> userDTO.getUserName()).collect(Collectors.toList());
        List<String> userNamesRegistered = userService.findExistingUserNameIn(universityUserNames);
        List<UserDTO> usersNotRegistered = universityUsers.stream().filter(userDTO -> !userNamesRegistered.contains(userDTO.getUserName())).collect(Collectors.toList());

        if(!usersNotRegistered.isEmpty()) {

        List<UserEntity> userEntities = usersNotRegistered.stream().map(userDTO -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setUserName(userDTO.getUserName());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setFirstName(userDTO.getFirstName());
            userEntity.setLastName(userDTO.getLastName());
            userEntity.setIdNumber(userDTO.getIdNumber());
            userEntity.setPassword(userDTO.getPassword());
            return userEntity;
        }).collect(Collectors.toList());
        userService.saveUsers(userEntities);

        usersNotRegistered.forEach(userDto -> {
            Map<String, String> paramsMoodle = new HashMap<>();
            paramsMoodle.put("users[0][username]",userDto.getUserName());
            paramsMoodle.put("users[0][password]",userDto.getPassword());
            paramsMoodle.put("users[0][firstname]",userDto.getFirstName());
            paramsMoodle.put("users[0][lastname]",userDto.getLastName());
            paramsMoodle.put("users[0][email]",userDto.getEmail());
            paramsMoodle.put("users[0][idnumber]",userDto.getIdNumber());

            List<MoodleResponseUsersDTO> moodleResponseUsersDTO = restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.CREATE_USERS.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<List<MoodleResponseUsersDTO>>() {});

            moodleResponseUsersDTO.forEach(moodleId -> {
                userService.updateMoodleIdByUserName(moodleId.getId(),moodleId.getUsername());
            });
        });
        }

        List<CourseDTO> universityCourses = restClient.getApiDataListWithDynamicParams(urlConfig.getUniversityUrl()+universityEndpointsConfig.getCoursesEndpoint(),params, new ParameterizedTypeReference<List<CourseDTO>>() {});
        List<String> universityCoursesIds = universityCourses.stream().map(courseDTO -> courseDTO.getCourseId()).collect(Collectors.toList());
        List<String> coursesRegistered = courseService.findExistingCourseIdIn(universityCoursesIds);
        List<CourseDTO> universityCoursesNotRegistered = universityCourses.stream().filter(userDTO -> !coursesRegistered.contains(userDTO.getCourseId())).collect(Collectors.toList());

        if(!universityCoursesNotRegistered.isEmpty()) {

            List<CourseEntity> courseEntities = universityCoursesNotRegistered.stream().map(courseDTO -> {
                CourseEntity courseEntity = new CourseEntity();
                courseEntity.setCourseId(courseDTO.getCourseId());
                courseEntity.setFullName(courseDTO.getFullName());
                courseEntity.setShortName(courseDTO.getShortName());
                courseEntity.setCategoryId(courseDTO.getCategoryId());
                return courseEntity;
            }).collect(Collectors.toList());
            courseService.saveCourses(courseEntities);

            universityCoursesNotRegistered.forEach(courseDto -> {
                Map<String, String> paramsMoodle = new HashMap<>();
                paramsMoodle.put("courses[0][fullname]",courseDto.getFullName());
                paramsMoodle.put("courses[0][shortname]",courseDto.getShortName());
                paramsMoodle.put("courses[0][categoryid]",courseDto.getCategoryId());
                paramsMoodle.put("courses[0][idnumber]",courseDto.getCourseId());

                List<MoodleResponseCoursesDTO> moodleResponseCoursesDTO = restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.CREATE_COURSES.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<List<MoodleResponseCoursesDTO>>() {});
                moodleResponseCoursesDTO.forEach(moodleId -> {
                    courseService.updateMoodleIdByShortName(moodleId.getId(),moodleId.getShortname());
                });
            });
        }

        List<RegisterDTO> universityRegisters = restClient.getApiDataListWithDynamicParams(urlConfig.getUniversityUrl()+universityEndpointsConfig.getRegistrationsEndpoint(),params, new ParameterizedTypeReference<List<RegisterDTO>>() {});
        List<RegisterDTO> universityUnregisteredRegisters = universityRegisters.stream().filter(registerDTO -> !registerService.findExistingRegisterIn(registerDTO.getUserName(),registerDTO.getCourseId())).collect(Collectors.toList());
        if(!universityUnregisteredRegisters.isEmpty()) {

            List<RegisterEntity> registerEntities = universityUnregisteredRegisters.stream().map(registerDTO -> {
                RegisterEntity registerEntity = new RegisterEntity();
                registerEntity.setCourseId(registerDTO.getCourseId());
                registerEntity.setUserName(registerDTO.getUserName());
                registerEntity.setMoodleCourseId(courseService.getMoodleIdByCourseId(registerDTO.getCourseId()));
                registerEntity.setUniversityRole(registerDTO.getRoleId());
                registerEntity.setMoodleRoleId(MoodleRolesEnum.valueOf(registerDTO.getRoleId()).getRoleId());
                return registerEntity;
            }).collect(Collectors.toList());
            registerService.saveRegisters(registerEntities);

            universityUnregisteredRegisters.forEach(registerDto -> {
                Map<String, String> paramsMoodle = new HashMap<>();
                paramsMoodle.put("enrolments[0][roleid]",MoodleRolesEnum.valueOf(registerDto.getRoleId()).getRoleId().toString());
                paramsMoodle.put("enrolments[0][userid]",userService.getMoodleIdByUserName(registerDto.getUserName()));
                paramsMoodle.put("enrolments[0][courseid]",courseService.getMoodleIdByCourseId(registerDto.getCourseId()));

                restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.ENROLL_USERS.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<List<String>>() {});

            });
        }






    }

}