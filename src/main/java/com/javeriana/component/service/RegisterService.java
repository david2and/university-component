package com.javeriana.component.service;

import com.javeriana.component.model.entity.RegisterEntity;
import com.javeriana.component.repository.RegistersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterService {

    @Autowired
    RegistersRepository registersRepository;

    public void saveRegister(RegisterEntity registerEntity){
        registersRepository.save(registerEntity);
    }

    public void saveRegisters(List<RegisterEntity> registers){
        registersRepository.saveAll(registers);
    }

    public boolean  findExistingRegisterIn (String userName, String courseId ){
        return registersRepository.findExistingUserNames(userName,courseId);
    }

    public void deleteByUserNameAndCourseId(String userName, String courseId){ registersRepository.deleteByUserNameAndCourseId(userName,courseId);}
}
