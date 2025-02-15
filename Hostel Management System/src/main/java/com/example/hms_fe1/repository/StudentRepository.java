package com.example.hms_fe1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hms_fe1.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {

}
