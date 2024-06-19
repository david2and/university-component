package com.javeriana.component.config;

import lombok.Getter;

@Getter
public enum MoodleFunctionsEnum {
    CREATE_USERS("core_user_create_users"),
    CREATE_COURSES("core_course_create_courses"),
    ENROLL_USERS("enrol_manual_enrol_users"),
    CREATE_CATEGORIES("core_course_create_categories"),
    GET_COURSES("core_course_get_courses"),
    GRADE_REPORT("gradereport_overview_get_course_grades");

    String moodleFunction;

    MoodleFunctionsEnum(String moodleFunction) {
        this.moodleFunction = moodleFunction;
    }
}
