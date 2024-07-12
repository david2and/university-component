package com.javeriana.component.service;

import com.javeriana.component.config.MoodleFunctionsEnum;
import com.javeriana.component.config.UniversityEndpointsConfig;
import com.javeriana.component.config.UrlConfig;
import com.javeriana.component.model.MoodleRolesEnum;
import com.javeriana.component.model.dto.*;
import com.javeriana.component.model.entity.CategoryEntity;
import com.javeriana.component.model.entity.CourseEntity;
import com.javeriana.component.model.entity.RegisterEntity;
import com.javeriana.component.model.entity.UserEntity;
import com.javeriana.component.model.request.CoursesSaveRequest;
import com.javeriana.component.model.request.RegistersSaveRequest;
import com.javeriana.component.model.request.UsersSaveRequest;
import com.javeriana.component.model.response.*;
import com.javeriana.component.rest.RestClient;
import com.javeriana.component.utils.PasswordHashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class MoodleService {

    private static final Logger logger = Logger.getLogger(RestClient.class.getName());



    @Autowired
    UniversityEndpointsConfig universityEndpointsConfig;

    @Autowired
    UrlConfig urlConfig;

    @Autowired
    private RestClient restClient;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    RegisterService registerService;

    @Autowired
    CategoryService categoryService;


    public StudentGradesResponse getGrades() {

        List<UserEntity> userEntities = userService.findAll();
        StudentGradesResponse studentGradesResponse = new StudentGradesResponse();

        List<StudentResponse> studentResponses = userEntities.stream().map(userEntity -> {
            Map<String, String> paramsMoodle = new HashMap<>();
            paramsMoodle.put("userid",userEntity.getMoodleId().toString());
            GradesResponseDTO gradesResponseDTOS = (GradesResponseDTO) restClient.getApiDataWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.GRADE_REPORT.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<GradesResponseDTO>() {});

            StudentResponse studentResponse = new StudentResponse();


            List<Grades> grades = gradesResponseDTOS.getGrades().stream().map(gradesResponseDTO -> {
                Grades gradesResponse = new Grades();
                gradesResponse.setCourseId(courseService.getCourseIdByMoodleId(gradesResponseDTO.getCourseid().toString()));
                gradesResponse.setGrade(gradesResponseDTO.getGrade());
                gradesResponse.setRawGrade(gradesResponseDTO.getRawgrade());
                return gradesResponse;

            }).collect(Collectors.toList());
            studentResponse.setGrades(grades);
            studentResponse.setUsername(userEntity.getUserName());
            return studentResponse;

        }).collect(Collectors.toList());
        studentGradesResponse.setStudents(studentResponses);

        return studentGradesResponse;

    }

    public List<CoursesResponse> getCourses(){
        List<CourseEntity> courseEntities = courseService.findAllCourses();
        return courseEntities.stream().map(courseEntity ->{
                    CoursesResponse coursesResponse = new CoursesResponse();
                    coursesResponse.setIdCourse(courseEntity.getCourseId());
                    coursesResponse.setName(courseEntity.getFullName());
                    coursesResponse.setArea(courseEntity.getCategoryName());
                    coursesResponse.setSync(courseEntity.getSync());
                    return coursesResponse;
                }).toList();
    }

    @Transactional
    public List<UsersSaveResponse> saveUsers(List<UsersSaveRequest> usersSaveRequests) {

        List<UserDTO> universityUsers = usersSaveRequests.stream().map(usersSaveRequest ->{
                    UserDTO userDTO = new UserDTO();
                    userDTO.setEmail(usersSaveRequest.getEmail());
                    userDTO.setUserName(usersSaveRequest.getUserName());
                    userDTO.setPassword(usersSaveRequest.getPassword());
                    userDTO.setFirstName(usersSaveRequest.getFirstName());
                    userDTO.setLastName(usersSaveRequest.getLastName());
                    userDTO.setIdNumber(usersSaveRequest.getIdNumber());
                    return userDTO;
                }
                ).toList();
        List<String> universityUserNames = universityUsers.stream().map(UserDTO::getUserName).collect(Collectors.toList());
        List<String> userNamesRegistered = userService.findExistingUserNameIn(universityUserNames);
        List<UserDTO> usersNotRegistered = universityUsers.stream().filter(userDTO -> !userNamesRegistered.contains(userDTO.getUserName())).collect(Collectors.toList());
        List<UsersSaveResponse> usersSaveResponses = new ArrayList<>();
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
                        UsersSaveResponse usersSaveResponse = new UsersSaveResponse();
                        usersSaveResponse.setUserName(moodleId.getUsername());
                        usersSaveResponse.setMoodleId(moodleId.getId());
                        usersSaveResponses.add(usersSaveResponse);
                    });
                }else{
                    userService.deleteUserByUsername(userDto.getUserName());
                }
            });


        }
        return usersSaveResponses;
    }

    @Transactional
    public List<CoursesSaveResponse> saveCourses(List<CoursesSaveRequest> coursesSaveRequests) {

        List<CoursesSaveResponse> coursesSaveResponses = new ArrayList<>();

        List<CategoriesDTO> universityCategories = new ArrayList<>();
        List<CourseDTO> universityCourses = new ArrayList<>();

        coursesSaveRequests.forEach(coursesSaveRequest -> {
            CategoriesDTO categoriesDTO = new CategoriesDTO();
            categoriesDTO.setName(coursesSaveRequest.getCategorySaveRequest().getName());
            categoriesDTO.setDescription(coursesSaveRequest.getCategorySaveRequest().getDescription());
            categoriesDTO.setParent(coursesSaveRequest.getParent());
            universityCategories.add(categoriesDTO);

            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(coursesSaveRequest.getCourseId());
            courseDTO.setFullName(coursesSaveRequest.getName());
            courseDTO.setCategoryName(coursesSaveRequest.getCategorySaveRequest().getName());
            courseDTO.setShortName(coursesSaveRequest.getShortName());
            universityCourses.add(courseDTO);


        });

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
                String categoryMoodleId = categoryService.findMoodleIdByCategoryName(courseDto.getCategoryName());
                Map<String, String> paramsMoodle = new HashMap<>();
                paramsMoodle.put("courses[0][fullname]",courseDto.getFullName());
                paramsMoodle.put("courses[0][shortname]",courseDto.getShortName());
                paramsMoodle.put("courses[0][categoryid]",categoryMoodleId);
                paramsMoodle.put("courses[0][idnumber]",courseDto.getCourseId());

                List<MoodleResponseCoursesDTO> moodleResponseCoursesDTO = restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl()+ MoodleFunctionsEnum.CREATE_COURSES.getMoodleFunction(),paramsMoodle, new ParameterizedTypeReference<List<MoodleResponseCoursesDTO>>() {});
                if(moodleResponseCoursesDTO!=null) {
                    moodleResponseCoursesDTO.forEach(moodleId -> {
                        courseService.updateMoodleIdByShortName(moodleId.getId(), moodleId.getShortname());
                        CategorySaveResponse categorySaveResponse = new CategorySaveResponse();
                        categorySaveResponse.setName(categoryService.findNameByMoodleId(categoryMoodleId));
                        categorySaveResponse.setMoodleId(categoryMoodleId);

                        CoursesSaveResponse coursesSaveResponse = new CoursesSaveResponse();
                        coursesSaveResponse.setShortName(moodleId.getShortname());
                        coursesSaveResponse.setMoodleId(moodleId.getId());
                        coursesSaveResponse.setCategorySaveResponse(categorySaveResponse);

                        coursesSaveResponses.add(coursesSaveResponse);

                    });
                }else{
                    courseService.deleteCourseByShortName(courseDto.getShortName());
                }
            });
        }

        return coursesSaveResponses;
    }
    @Transactional
    public void saveRegisters(List<RegistersSaveRequest> registersSaveRequests) {

        List<RegisterDTO> universityRegisters = registersSaveRequests.stream().map(registersSaveRequest -> {
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setCourseId(registersSaveRequest.getCourseId());
            registerDTO.setUserName(registersSaveRequest.getUserName());
            registerDTO.setRoleId(registersSaveRequest.getRoleId());
            return registerDTO;
        }).toList();
        List<RegisterDTO> universityUnregisteredRegisters = universityRegisters.stream().filter(registerDTO -> !registerService.findExistingRegisterIn(registerDTO.getUserName(), registerDTO.getCourseId())).collect(Collectors.toList());



        if (!universityUnregisteredRegisters.isEmpty()) {

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
                paramsMoodle.put("enrolments[0][roleid]", MoodleRolesEnum.valueOf(registerDto.getRoleId()).getRoleId().toString());
                paramsMoodle.put("enrolments[0][userid]", userId);
                paramsMoodle.put("enrolments[0][courseid]", courseId);


                restClient.getApiDataListWithDynamicParams(urlConfig.getMoodleUrl() + MoodleFunctionsEnum.ENROLL_USERS.getMoodleFunction(), paramsMoodle, new ParameterizedTypeReference<List<String>>() {
                });

                if (userId == null || courseId == null) {
                    logger.log(Level.SEVERE, "An error ocurred requesting to Moodle API for register the User " + registerDto.getUserName() + " to the Course with ID " + registerDto.getCourseId());
                    registerService.deleteByUserNameAndCourseId(registerDto.getUserName(), registerDto.getCourseId());
                }


            });
        }
    }
}
