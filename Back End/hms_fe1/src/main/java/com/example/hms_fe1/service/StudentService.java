package com.example.hms_fe1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.Student;
import com.example.hms_fe1.repository.StudentRepository;

@Service
public class StudentService {
	
    @Autowired
    private StudentRepository studentRepository;

    public void save(Student student) {
        studentRepository.save(student);
    }
}

