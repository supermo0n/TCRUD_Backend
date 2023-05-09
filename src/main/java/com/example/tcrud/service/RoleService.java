package com.example.tcrud.service;

import com.example.tcrud.model.ERole;
import com.example.tcrud.model.Role;
import com.example.tcrud.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Optional<Role> findByName(ERole name)
    {
        return roleRepository.findByName(name);
    }
}









