package com.example.hms_fe1.service;

import java.util.List;

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
    
    public List<Student> getAllStudents(){
    	return studentRepository.findAll();
    }
    
    public int getStudentCount() {
    	return studentRepository.studentCount();
    }
    
    public Student findStudentById(String id) {
    	return studentRepository.findById(id) .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}

