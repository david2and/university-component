package com.javeriana.component.config;

import lombok.Getter;

@Getter
public enum MoodleFunctionsEnum {
    CREATE_USERS("core_user_create_users"),
    CREATE_COURSES("core_course_create_courses"),
    ENROLL_USERS("enrol_manual_enrol_users"),
    GRADE_REPORT("gradereport_overview_get_course_grades");

    String moodleFunction;

    MoodleFunctionsEnum(String moodleFunction) {
        this.moodleFunction = moodleFunction;
    }
}
