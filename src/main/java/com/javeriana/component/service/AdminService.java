package com.javeriana.component.service;

import com.javeriana.component.model.entity.AdminEntity;
import com.javeriana.component.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    AdminRepository adminRepository;

    public void saveAdminUser(AdminEntity adminEntity){
        adminRepository.save(adminEntity);
    }

    public String validateAdminUser(String username,  String password){
        return adminRepository.validateUser(username,password);
    }
}
