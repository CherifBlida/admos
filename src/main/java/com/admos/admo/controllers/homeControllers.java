package com.admos.admo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class homeControllers {
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Hello, World!");
        return "home"; // This corresponds to the name of your Thymeleaf template (home.html)
    }
   /* @GetMapping("/questions22")
    public String show_questions(){

        return "question";
    }
    @GetMapping("/questions2")
    public String show_questions2(){

        return "questions_2";
    }*/
;}
