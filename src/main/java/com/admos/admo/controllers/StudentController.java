package com.admos.admo.controllers;

import com.admos.admo.entities.Chapter;
import com.admos.admo.services.ChapterService;
import com.admos.admo.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import com.admos.admo.entities.Subject;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final SubjectService subjectService;
    private final ChapterService chapterService;

    @Autowired
    public StudentController(SubjectService subjectService, ChapterService chapterService) {
        this.subjectService = subjectService;
        this.chapterService = chapterService;
    }

    @GetMapping("/quiz/setup")
    public String getQuizSetupPage(Model model) {
        // Fetch all subjects
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);

        // Fetch all chapters (or filter by a default subject if preferred)
        List<Chapter> chapters = chapterService.getAllChapters(); // Assume this method exists
        model.addAttribute("chapters", chapters);

        // Empty selected chapters for now
        model.addAttribute("selectedChapters", new ArrayList<>());

        return "student_quiz_setup";
    }
}