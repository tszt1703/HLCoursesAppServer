package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.ProgressStat;
import org.example.hlcoursesappserver.repository.ProgressStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressStatService {

    private final ProgressStatRepository progressStatRepository;

    @Autowired
    public ProgressStatService(ProgressStatRepository progressStatRepository) {
        this.progressStatRepository = progressStatRepository;
    }

    public ProgressStat createProgressStat(Long listenerId, Long courseId) {
        ProgressStat progress = new ProgressStat(listenerId, courseId);
        return progressStatRepository.save(progress);
    }
}