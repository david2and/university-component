package com.javeriana.component.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "courses")
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String fullName;
    private String shortName;
    private String categoryName;
    private String courseId;
    @Column(unique = true)
    private Integer moodleId;
    private Boolean sync;

}
