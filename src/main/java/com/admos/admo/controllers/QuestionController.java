package com.admos.admo.controllers;

import com.admos.admo.DTO.AnswerDto;
import com.admos.admo.DTO.ChapterDTO;
import com.admos.admo.DTO.QuestionDto;
import com.admos.admo.DTO.SubjectDTO;
import com.admos.admo.entities.Answer;
import com.admos.admo.entities.Question;
import com.admos.admo.repository.ChapterRepository;
import com.admos.admo.repository.QuestionRepository;
import com.admos.admo.services.ChapterService;
import com.admos.admo.services.QuestionService;
import com.admos.admo.services.SubjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final SubjectService subjectService;
    private final ChapterService chapterService;
    private final QuestionRepository questionRepository; // Added
    private final ChapterRepository chapterRepository;   // Added

    @Autowired
    public QuestionController(QuestionService questionService, SubjectService subjectService,
                              ChapterService chapterService,QuestionRepository questionRepository,
                              ChapterRepository chapterRepository) {
        this.questionService = questionService;
        this.subjectService = subjectService;
        this.chapterService = chapterService;
        this.questionRepository = questionRepository;
        this.chapterRepository = chapterRepository;
    }

    @GetMapping
    public String getQuestionsPage(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "questions_2";
    }


    @PostMapping("/subjects")
    @ResponseBody
    public SubjectDTO createSubject(@Valid @RequestBody SubjectDTO dto) {
        return subjectService.createSubject(dto);
    }

    @GetMapping("/chapters")
    @ResponseBody
    public List<ChapterDTO> getChapters(@RequestParam Long subjectId) {
        return chapterService.getChaptersBySubjectId(subjectId).stream()
                .map(c -> {
                    ChapterDTO dto = new ChapterDTO();
                    dto.setId(c.getId());
                    dto.setName(c.getName());
                    dto.setSubjectId(c.getSubject().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/chapters")
    @ResponseBody
    public ChapterDTO createChapter(@Valid @RequestBody ChapterDTO dto) {
        return chapterService.createChapter(dto);
    }

    @PostMapping
    @ResponseBody
    public QuestionDto createQuestion(@Valid @RequestBody QuestionDto dto) {
        return questionService.createQuestion(dto);
    }
    @PostMapping("/import")
    @ResponseBody
    public List<QuestionDto> importQuestions(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Received file: " + file.getOriginalFilename());
        List<QuestionDto> result = questionService.importFromDocx(file);
        System.out.println("Returning questions: " + result.size());
        return result;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public QuestionDto getQuestion(@PathVariable Long id) {
        System.out.println("Received GET /questions/" + id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setChapterId(question.getChapter().getId());
        dto.setSubjectId(question.getChapter().getSubject().getId()); // Add subjectId
        question.getAnswers().forEach(a -> {
            AnswerDto adto = new AnswerDto();
            adto.setId(a.getId());
            adto.setText(a.getText());
            adto.setCorrect(a.isCorrect());
            adto.setExplanation(a.getExplanation());
            dto.getAnswers().add(adto);
        });
        System.out.println("Returning question: " + dto.getId());
        return dto;
    }

    @PutMapping("/{id}")
    @ResponseBody
    public QuestionDto updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionDto dto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        question.setText(dto.getText());
        question.setChapter(chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found")));
        question.getAnswers().clear();
        dto.getAnswers().forEach(adto -> {
            Answer answer = new Answer();
            answer.setText(adto.getText());
            answer.setCorrect(adto.isCorrect());
            answer.setExplanation(adto.getExplanation());
            question.addAnswer(answer);
        });
        Question saved = questionRepository.save(question);
        QuestionDto result = new QuestionDto();
        result.setId(saved.getId());
        result.setText(saved.getText());
        result.setChapterId(saved.getChapter().getId());
        saved.getAnswers().forEach(a -> {
            AnswerDto adto = new AnswerDto();
            adto.setId(a.getId());
            adto.setText(a.getText());
            adto.setCorrect(a.isCorrect());
            adto.setExplanation(a.getExplanation());
            result.getAnswers().add(adto);
        });
        return result;
    }
}