package com.admos.admo.services;

import com.admos.admo.DTO.SubjectDTO;
import com.admos.admo.entities.Subject;
import com.admos.admo.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional
    public SubjectDTO createSubject(SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setName(dto.getName());
        Subject saved = subjectRepository.save(subject);
        SubjectDTO result = new SubjectDTO();
        result.setId(saved.getId());
        result.setName(saved.getName());
        return result;
    }
}