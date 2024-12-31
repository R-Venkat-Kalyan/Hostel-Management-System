package com.example.hms_fe1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hms_fe1.entity.IssueEntity;

@Repository
public interface IssueRepository extends JpaRepository<IssueEntity, Integer> {

}
