package com.admos.admo.services;

import com.admos.admo.DTO.ChapterDTO;
import com.admos.admo.entities.Chapter;
import com.admos.admo.entities.Subject;
import com.admos.admo.repository.ChapterRepository;
import com.admos.admo.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository;



    @Autowired
    public ChapterService(ChapterRepository chapterRepository, SubjectRepository subjectRepository) {
        this.chapterRepository = chapterRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Chapter> getChaptersBySubjectId(Long subjectId) {
        return chapterRepository.findBySubjectId(subjectId);
    }

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    @Transactional
    public ChapterDTO createChapter(ChapterDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        Chapter chapter = new Chapter();
        chapter.setName(dto.getName());
        chapter.setSubject(subject);
        Chapter saved = chapterRepository.save(chapter);
        return new ChapterDTO() {{ setId(saved.getId()); setName(saved.getName()); setSubjectId(saved.getSubject().getId()); }};
    }
}
