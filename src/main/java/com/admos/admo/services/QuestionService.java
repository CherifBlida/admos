package com.admos.admo.services;

import com.admos.admo.DTO.AnswerDto;
import com.admos.admo.DTO.QuestionDto;
import com.admos.admo.entities.Answer;
import com.admos.admo.entities.Chapter;
import com.admos.admo.entities.Question;
import com.admos.admo.entities.Subject;
import com.admos.admo.repository.ChapterRepository;
import com.admos.admo.repository.QuestionRepository;
import com.admos.admo.repository.SubjectRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository;
    //private final ModelMapper modelMapper;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, ChapterRepository chapterRepository,
                           //ModelMapper modelMapper,
                           SubjectRepository subjectRepository) {
        this.questionRepository = questionRepository;
        this.chapterRepository = chapterRepository;
        //this.modelMapper = modelMapper;
        this.subjectRepository = subjectRepository;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    @Transactional
    public QuestionDto createQuestion(QuestionDto dto) {
        Chapter chapter = chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        Question question = new Question();
        question.setText(dto.getText());
        question.setChapter(chapter);
        dto.getAnswers().forEach(adto -> {
            Answer answer = new Answer();
            answer.setText(adto.getText());
            answer.setCorrect(adto.isCorrect());
            answer.setExplanation(adto.getExplanation());
            question.addAnswer(answer);
        });
        Question saved = questionRepository.save(question); // Line 43
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

    @Transactional
    public List<QuestionDto> importFromDocx(MultipartFile file) throws IOException {
        Subject defaultSubject = subjectRepository.findByName("Imported")
                .orElseGet(() -> {
                    Subject s = new Subject();
                    s.setName("Imported");
                    return subjectRepository.save(s);
                });
        Chapter defaultChapter = chapterRepository.findBySubjectId(defaultSubject.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    Chapter c = new Chapter();
                    c.setName("Imported");
                    c.setSubject(defaultSubject);
                    return chapterRepository.save(c);
                });

        List<Question> questions = parseDocx(file, defaultChapter);
        System.out.println("Saving " + questions.size() + " questions");
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        System.out.println("Saved " + savedQuestions.size() + " questions");
        return savedQuestions.stream().map(q -> {
            QuestionDto dto = new QuestionDto();
            dto.setId(q.getId());
            dto.setText(q.getText());
            dto.setChapterId(q.getChapter().getId());
            q.getAnswers().forEach(a -> {
                AnswerDto adto = new AnswerDto();
                adto.setId(a.getId());
                adto.setText(a.getText());
                adto.setCorrect(a.isCorrect());
                adto.setExplanation(a.getExplanation());
                dto.getAnswers().add(adto);
            });
            return dto;
        }).collect(Collectors.toList());
    }
    private List<Question> parseDocx(MultipartFile file, Chapter defaultChapter) throws IOException {
        List<Question> questions = new ArrayList<>();
        XWPFDocument doc = new XWPFDocument(file.getInputStream());
        StringBuilder text = new StringBuilder();
        for (XWPFParagraph para : doc.getParagraphs()) {
            String paraText = para.getText().trim();
            if (!paraText.isEmpty()) {
                text.append(paraText).append("\n");
            }
        }
        doc.close();
        System.out.println("Parsed DOCX text: " + text.toString());

        String[] lines = text.toString().split("\n");
        Question currentQuestion = null;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            if (line.matches("^\\d+-.*")) { // New question starts with number-hyphen
                if (currentQuestion != null) {
                    questions.add(currentQuestion);
                }
                currentQuestion = new Question();
                String[] parts = line.split("\\s+(?=[a-z]-)", 2);
                String questionText = parts[0].replaceFirst("^\\d+-", "").trim();
                currentQuestion.setText(questionText);
                currentQuestion.setChapter(defaultChapter);
                if (parts.length > 1) {
                    String[] answerParts = parts[1].split("\\s+(?=[a-z]-)");
                    for (String answerText : answerParts) {
                        Answer answer = new Answer();
                        answer.setText(answerText.replaceFirst("^[a-z]-", "").trim());
                        answer.setCorrect(false);
                        currentQuestion.addAnswer(answer);
                    }
                }
            } else if (currentQuestion != null && line.matches("^[a-z]-.*")) { // Additional answers
                String[] answerParts = line.split("\\s+(?=[a-z]-)");
                for (String answerText : answerParts) {
                    Answer answer = new Answer();
                    answer.setText(answerText.replaceFirst("^[a-z]-", "").trim());
                    answer.setCorrect(false);
                    currentQuestion.addAnswer(answer);
                }
            }
        }
        if (currentQuestion != null) {
            questions.add(currentQuestion);
        }
        System.out.println("Parsed questions: " + questions.size());
        return questions;
    }

  /*  private List<Question> parseDocx(MultipartFile file, Chapter defaultChapter) throws IOException {
        List<Question> questions = new ArrayList<>();
        XWPFDocument doc = new XWPFDocument(file.getInputStream());
        String text = doc.getParagraphs().stream()
                .map(XWPFParagraph::getText)
                .filter(t -> !t.trim().isEmpty())
                .collect(Collectors.joining(" "));
        doc.close();
        System.out.println("Parsed DOCX text: " + text);

        String[] parts = text.split("(?=[a-z]-)");
        if (parts.length < 2) {
            System.out.println("No valid question format found");
            return questions;
        }

        Question question = new Question();
        String questionText = parts[0].replaceFirst("^\\d+-", "").trim();
        question.setText(questionText);
        question.setChapter(defaultChapter);

        for (int i = 1; i < parts.length; i++) {
            String answerText = parts[i].replaceFirst("^[a-z]-", "").trim();
            Answer answer = new Answer();
            answer.setText(answerText);
            answer.setCorrect(false);
            question.addAnswer(answer);
        }
        questions.add(question);
        System.out.println("Parsed questions: " + questions.size());
        return questions;
    }*/
    /*@Transactional
    public QuestionDto createQuestion(QuestionDto dto) {
        Chapter chapter = chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        Question question = modelMapper.map(dto, Question.class);
        question.setChapter(chapter);
        question.getAnswers().forEach(a -> a.setQuestion(question));
        Question saved = questionRepository.save(question);
        return modelMapper.map(saved, QuestionDto.class);
    }*/
}