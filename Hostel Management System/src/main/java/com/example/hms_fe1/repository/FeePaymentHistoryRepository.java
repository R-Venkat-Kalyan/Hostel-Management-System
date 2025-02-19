package com.example.hms_fe1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hms_fe1.entity.FeePaymentHistory;

@Repository
public interface FeePaymentHistoryRepository extends JpaRepository<FeePaymentHistory, Long> {

}
