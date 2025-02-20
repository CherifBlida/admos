package com.admos.admo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Chapter extends BaseEntity {
   @NotBlank
   @Size(max = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

   @ManyToMany(mappedBy = "chapters")
   private List<Quiz> quizzes = new ArrayList<>();

    // Bidirectional relationship helper
   /* public void addQuestion(Question question) {
       questions.add(question);
       question.setChapter(this);
    }*/
    // Helper methods
    public void addQuestion(Question question) {
        questions.add(question);
        question.setChapter(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setChapter(null);
    }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Subject getSubject() {
      return subject;
   }

   public void setSubject(Subject subject) {
      this.subject = subject;
   }

   public List<Question> getQuestions() {
      return questions;
   }

   public void setQuestions(List<Question> questions) {
      this.questions = questions;
   }
}