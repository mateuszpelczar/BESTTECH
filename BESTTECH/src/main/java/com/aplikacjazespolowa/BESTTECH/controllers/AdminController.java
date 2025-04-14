package com.aplikacjazespolowa.BESTTECH.controllers;

import com.aplikacjazespolowa.BESTTECH.models.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DBUserRepository userRepository;
    @Autowired
    private DBRoleRepository roleRepository;
    @Autowired
    private LogsRepository logsRepository;
    @Autowired
    private HttpServletRequest request; //aby pobrac aktualnego uzytkownika;



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

    @GetMapping("/manageemployess")
    public String manageEmployees(Model model){
        List<DBUser> employees = userRepository.findByRoles_Name("EMPLOYEE");
        model.addAttribute("employees",employees);
        return "admin/manage_employees";
    }

    @GetMapping("/deleteemployee")
    public String deleteEmployee(Model model){
        List<DBUser> employees = userRepository.findByRoles_Name("EMPLOYEE");
        model.addAttribute("employees",employees);
        return "admin/delete_employee";
    }

    @PostMapping("/deleteemployee")
    public String deletedEmployee(@RequestParam Integer userId, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Optional<DBUser> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            DBUser user = userOptional.get();

            //dodanie loga przez usunieciem
            String currentUser = request.getUserPrincipal().getName();  //aktualnie zalogowany user
            String deletedEmail = user.getEmail();  //email usuwanego
            logsRepository.save(new LogsSystem("Usunieto pracownika: "+deletedEmail, currentUser, "WARN"));

            userRepository.deleteById(userId);
            redirectAttributes.addFlashAttribute("message", "Użytkownik został pomyślnie usunięty.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Użytkownik o podanym ID nie istnieje.");
        }




        return "redirect:/admin/deleteemployee";
    }

    @GetMapping("/admin/logs")
    public String pokazLogi(Model model){
        List<LogsSystem>logi = logsRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        model.addAttribute("logi", logi);
        return "admin/logs";
    }





}
