package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.FeedBack;
import com.hms.meenakshi.repository.FeedBackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final FeedBackRepository feedBackRepository;
    public void save(FeedBack feedBack){
        feedBackRepository.save(feedBack);
    }
}
