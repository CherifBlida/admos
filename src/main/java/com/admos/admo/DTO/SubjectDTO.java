package com.admos.admo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubjectDTO extends BaseDTO{
    // For creating/updating subjects
    @NotBlank(message = "Subject name is required")
    @Size(max = 100, message = "Subject name must be 100 characters or less")
    private String name;
    private List<Long> chapterIds = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getChapterIds() {
        return chapterIds;
    }

    public void setChapterIds(List<Long> chapterIds) {
        this.chapterIds = chapterIds;
    }
}
