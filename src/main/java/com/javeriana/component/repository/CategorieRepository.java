package com.javeriana.component.repository;

import com.javeriana.component.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategorieRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT u.name FROM CategoryEntity u WHERE u.name IN :categorynames")
    List<String> findExistingCategoryNames(@Param("categorynames")List<String> categorynames);

    @Query("SELECT u.moodleId FROM CategoryEntity u WHERE u.name = :categoryname")
    String findMoodleIdByCategoryName(@Param("categoryname")String categoryName);

    @Modifying
    @Transactional
    @Query("UPDATE CategoryEntity u SET u.moodleId = :id WHERE u.name = :categoryname")
    void updateMoodleIdByCategoryName(@Param("id") Integer id, @Param("categoryname") String categoryname);

    void deleteByName(String name);

    @Query("SELECT u.name FROM CategoryEntity u WHERE u.moodleId = :moodleId")
    String findNameByMoodleId(String moodleId);
}
