package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DBUserRepository userRepository;
    @Autowired
    private DBRoleRepository roleRepository;


    @GetMapping
    public String adminPanel() {
        return "admin/adminPanel";
    }

    @GetMapping("/manageusers")
    public String manageUsers(Model model) {

        List<DBUser> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/manage_users";
    }

    @GetMapping("/manageusers/changerole")
    public String changeUserRole(@RequestParam Integer userId, Model model) {

        DBUser user = userRepository.findById(userId).orElseThrow();
        List<DBRole> allRoles = roleRepository.findAll();


        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin/change_role";
    }

    @PostMapping("/manageusers/changerole/save")
    public String saveUserRoles(@RequestParam Integer userId,
                                @RequestParam(required = false) List<Integer> roleIds) {

        DBUser user = userRepository.findById(userId).orElseThrow();

        Set<DBRole> newRoles = new HashSet<>();
        if (roleIds != null) {
            newRoles.addAll(roleRepository.findAllById(roleIds));
        }

        user.setRoles(newRoles);
        userRepository.save(user);

        return "redirect:/admin/manageusers";
    }
}
