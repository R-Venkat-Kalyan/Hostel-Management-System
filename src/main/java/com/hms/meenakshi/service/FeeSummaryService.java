package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.FeeSummary;
import com.hms.meenakshi.repository.FeeSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeeSummaryService {

    private final FeeSummaryRepository feeSummaryRepository;


    public Optional<FeeSummary> getSummaryById(String feeSummaryId) {
        return feeSummaryRepository.findById(feeSummaryId);
    }
}
