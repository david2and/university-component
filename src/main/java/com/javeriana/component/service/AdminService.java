package com.javeriana.component.service;

import com.javeriana.component.model.AdminInfoDetails;
import com.javeriana.component.model.entity.AdminEntity;
import com.javeriana.component.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


@Service
public class AdminService implements UserDetailsService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    PasswordEncoder encoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AdminEntity> userDetail = adminRepository.findByUserName(username);

        // Converting userDetail to UserDetails
        return userDetail.map(AdminInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public void saveAdminUser(AdminEntity adminEntity){
        adminEntity.setRoles("Admin");
        adminEntity.setPassword(encoder.encode(adminEntity.getPassword()));
        adminRepository.save(adminEntity);
    }

    public String validateAdminUser(String username,  String password){
        return adminRepository.validateUser(username,password);
    }
}
