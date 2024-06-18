package com.javeriana.component.service;

import com.javeriana.component.model.entity.UserEntity;
import com.javeriana.component.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(UserEntity userEntity){
        userRepository.save(userEntity);
    }

    public void saveUsers(List<UserEntity> users){
        userRepository.saveAll(users);
    }

    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    public List<String> findExistingUserNameIn(List<String> userNames){
        return userRepository.findExistingUserNames(userNames);
    }

    public void updateMoodleIdByUserName(Integer moodleId, String userName){
        userRepository.updateMoodleIdByUsername(moodleId,userName);
    }

    public String getMoodleIdByUserName(String userName){
        return userRepository.findMoodleIdByUserName(userName);
    }

}
