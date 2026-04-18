package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.VacatedResident;
import com.hms.meenakshi.repository.VacatedResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacatedResidentService {

    private final VacatedResidentRepository vacatedResidentRepository;

    public List<VacatedResident> getAllVacatedResidents() {
        // Fetches all vacated records sorted by most recent vacate date
        return vacatedResidentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(VacatedResident::getVacatedDate).reversed())
                .collect(Collectors.toList());
    }

    public long totalVacatedStudents(){
        return vacatedResidentRepository.count();
    }
}
