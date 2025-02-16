package com.example.hms_fe1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hms_fe1.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {

	@Query(value = "select count(*) from student", nativeQuery = true)
	int studentCount();
}
