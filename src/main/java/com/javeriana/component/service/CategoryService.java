package com.javeriana.component.service;

import com.javeriana.component.model.entity.CategoryEntity;
import com.javeriana.component.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategorieRepository categorieRepository;

    public void saveCategory(CategoryEntity categoryEntity){
        categorieRepository.save(categoryEntity);
    }

    public void saveCategories(List<CategoryEntity> categories){
        categorieRepository.saveAll(categories);
    }


    public List<String> findExistingCategoryNameIn(List<String> categoryNames){
        return categorieRepository.findExistingCategoryNames(categoryNames);
    }

    public String findMoodleIdByCategoryName(String name){
        return categorieRepository.findMoodleIdByCategoryName(name);
    }

    public String findNameByMoodleId(String moodleID){
        return categorieRepository.findNameByMoodleId(moodleID);
    }

    public void updateMoodleIdByCategoryName(Integer moodleId, String categoryName){
        categorieRepository.updateMoodleIdByCategoryName(moodleId,categoryName);
    }

    public void deleteCategoryByCategoryName(String categoryName){categorieRepository.deleteByName(categoryName);}

}
