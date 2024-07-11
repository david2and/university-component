package com.javeriana.component.service;

import com.javeriana.component.config.MoodleFunctionsEnum;
import com.javeriana.component.config.TaskSchedulingConfig;
import com.javeriana.component.config.UniversityEndpointsConfig;
import com.javeriana.component.config.UrlConfig;
import com.javeriana.component.model.MoodleRolesEnum;
import com.javeriana.component.model.dto.*;
import com.javeriana.component.model.entity.*;
import com.javeriana.component.model.request.AdminRequest;
import com.javeriana.component.model.request.CoursesRequest;
import com.javeriana.component.model.request.GradesRequest;
import com.javeriana.component.model.request.SyncRequest;
import com.javeriana.component.model.response.CoursesResponse;
import com.javeriana.component.model.response.StudentGradesResponse;
import com.javeriana.component.rest.RestClient;
import com.javeriana.component.utils.PasswordHashing;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class UniversityService {

    private static final Logger logger = Logger.getLogger(RestClient.class.getName());


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
    CategoryService categoryService;

    @Autowired
    MoodleService moodleService;

    @Autowired
    AdminService adminService;

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
        List<String> universityUserNames = universityUsers.stream().map(UserDTO::getUserName).collect(Collectors.toList());
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
            userEntity.setPassword(PasswordHashing.hashPassword(userDTO.getPassword()));
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

            if(moodleResponseUsersDTO!=null){
                moodleResponseUsersDTO.forEach(moodleId -> {
                    userService.updateMoodleIdByUserName(moodleId.getId(),moodleId.getUsername());
                });
            }else{
                userService.deleteUserByUsername(userDto.getUserName());
            }
        });
        }

        List<CategoriesDTO> universityCategories = restClient.getApiDataListWithDynamicParams(urlConfig.getUniversityUrl()+universityEndpointsConfig.getCategoriesEndpoint(),params, new ParameterizedTypeReference<List<CategoriesDTO>>() {});
        List<String> universityCategoriesNames = universityCategories.stream().map(CategoriesDTO::getName).toList();
        List<String> categoriesRegistered = categoryService.findExistingCategoryNameIn(universityCategoriesNames);
        List<CategoriesDTO> categoriesNotRegistered = universityCategories.stream().filter(categoriesDTO -> !categoriesRegistered.contains(categoriesDTO.getName())).toList();

        if(!categoriesNotRegistered.isEmpty()) {

            List<CategoryEntity> categoryEntities = categoriesNotRegistered.stream().map(categoryDTO -> {
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setName(categoryDTO.getName());
                categoryEntity.setDescription(categoryDTO.getDescription());
                return categoryEntity;
            }).collect(Collectors.toList());
            categoryService.saveCategories(categoryEntities);

            categoriesNotRegistered.forEach(categoriesDTO -> {
                Map<String, String> paramsMoodle = new HashMap<>();
                paramsMoodle.put("categories[0][name]",categoriesDTO.getName());
                paramsMoodle.put("categories[0][parent]", !Objects.equals(categoriesDTO.getParent(), "") ?categoryService.findMoodleIdByCategoryName(categoriesDTO.getName()):"0");
                paramsMoodle.put("categories[0][description]",categoriesDTO.getDescription());

                List<MoodleResponseCategoriesDTO> moodleResponseCategoriesDTO = restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.CREATE_CATEGORIES.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<List<MoodleResponseCategoriesDTO>>() {});
                if(moodleResponseCategoriesDTO!=null) {
                    moodleResponseCategoriesDTO.forEach(moodleId -> {
                        categoryService.updateMoodleIdByCategoryName(moodleId.getId(), moodleId.getName());
                    });
                }else{
                    categoryService.deleteCategoryByCategoryName(categoriesDTO.getName());
            }
            });
        }

        List<CourseDTO> universityCourses = restClient.getApiDataListWithDynamicParams(urlConfig.getUniversityUrl()+universityEndpointsConfig.getCoursesEndpoint(),params, new ParameterizedTypeReference<List<CourseDTO>>() {});
        List<String> universityCoursesIds = universityCourses.stream().map(CourseDTO::getCourseId).collect(Collectors.toList());
        List<String> coursesRegistered = courseService.findExistingCourseIdIn(universityCoursesIds);
        List<CourseDTO> universityCoursesNotRegistered = universityCourses.stream().filter(userDTO -> !coursesRegistered.contains(userDTO.getCourseId())).collect(Collectors.toList());

        if(!universityCoursesNotRegistered.isEmpty()) {

            List<CourseEntity> courseEntities = universityCoursesNotRegistered.stream().map(courseDTO -> {
                CourseEntity courseEntity = new CourseEntity();
                courseEntity.setCourseId(courseDTO.getCourseId());
                courseEntity.setFullName(courseDTO.getFullName());
                courseEntity.setShortName(courseDTO.getShortName());
                courseEntity.setCategoryName(courseDTO.getCategoryName());
                return courseEntity;
            }).collect(Collectors.toList());
            courseService.saveCourses(courseEntities);

            universityCoursesNotRegistered.forEach(courseDto -> {
                Map<String, String> paramsMoodle = new HashMap<>();
                paramsMoodle.put("courses[0][fullname]",courseDto.getFullName());
                paramsMoodle.put("courses[0][shortname]",courseDto.getShortName());
                paramsMoodle.put("courses[0][categoryid]",categoryService.findMoodleIdByCategoryName(courseDto.getCategoryName()));
                paramsMoodle.put("courses[0][idnumber]",courseDto.getCourseId());

                List<MoodleResponseCoursesDTO> moodleResponseCoursesDTO = restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.CREATE_COURSES.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<List<MoodleResponseCoursesDTO>>() {});
                if(moodleResponseCoursesDTO!=null) {
                    moodleResponseCoursesDTO.forEach(moodleId -> {
                        courseService.updateMoodleIdByShortName(moodleId.getId(), moodleId.getShortname());
                    });
                }else{
                    courseService.deleteCourseByShortName(courseDto.getShortName());
                }
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
                String userId = userService.getMoodleIdByUserName(registerDto.getUserName());
                String courseId = courseService.getMoodleIdByCourseId(registerDto.getCourseId());
                Map<String, String> paramsMoodle = new HashMap<>();
                paramsMoodle.put("enrolments[0][roleid]",MoodleRolesEnum.valueOf(registerDto.getRoleId()).getRoleId().toString());
                paramsMoodle.put("enrolments[0][userid]",userId);
                paramsMoodle.put("enrolments[0][courseid]",courseId);


                List<String> response = restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.ENROLL_USERS.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<List<String>>() {});

                if(userId==null || courseId==null ){
                    logger.log(Level.SEVERE,"An error ocurred requesting to Moodle API for register the User "+registerDto.getUserName()+" to the Course with ID "+registerDto.getCourseId());
                    registerService.deleteByUserNameAndCourseId(registerDto.getUserName(),registerDto.getCourseId());
                }



            });
        }
    }


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