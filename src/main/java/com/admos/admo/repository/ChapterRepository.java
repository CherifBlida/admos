package com.admos.admo.repository;

import com.admos.admo.entities.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findBySubjectId(Long subjectId);

    @Query("SELECT c FROM Chapter c WHERE c.subject.id = :subjectId")
    List<Chapter> getChaptersBySubject(Long subjectId);
}