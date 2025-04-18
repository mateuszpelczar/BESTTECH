//package com.aplikacjazespolowa.BESTTECH.controllers;
//
//import com.aplikacjazespolowa.BESTTECH.models.DBRole;
//import com.aplikacjazespolowa.BESTTECH.models.DBRoleRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final DBRoleRepository roleRepository;
//
//    public DataInitializer(DBRoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        if (roleRepository.findByName("ADMIN").isEmpty()) {
//            DBRole adminRole = new DBRole();
//            adminRole.setName("ADMIN");
//            roleRepository.save(adminRole);
//        }
//
//        if (roleRepository.findByName("EMPLOYEE").isEmpty()) {
//            DBRole employeeRole = new DBRole();
//            employeeRole.setName("EMPLOYEE");
//            roleRepository.save(employeeRole);
//        }
//
//        if (roleRepository.findByName("CLIENT").isEmpty()) {
//            DBRole clientRole = new DBRole();
//            clientRole.setName("CLIENT");
//            roleRepository.save(clientRole);
//        }
//    }
//}
