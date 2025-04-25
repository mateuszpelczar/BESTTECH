package com.aplikacjazespolowa.BESTTECH.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/opinie")
public class ReviewController {

    @GetMapping("")
    public String showOpiniePage(Model model) {
        return "reviews/review";
    }

    @GetMapping("/dodaj_opinie")
    public String showAddReviewPage(Model model) {
        return "reviews/add_review";
    }

    @GetMapping("/zarzadzaj_opiniami")
    public String showManageReviewsPage(Model model) {
        return "reviews/manage_review";
    }
}
