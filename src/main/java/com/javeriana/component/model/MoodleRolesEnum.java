package com.javeriana.component.model;

import lombok.Getter;

@Getter
public enum MoodleRolesEnum {
    TEACHER(4),
    STUDENT(5);

    Integer roleId;

    MoodleRolesEnum(Integer moodleFunction) {
        this.roleId = moodleFunction;
    }

}
