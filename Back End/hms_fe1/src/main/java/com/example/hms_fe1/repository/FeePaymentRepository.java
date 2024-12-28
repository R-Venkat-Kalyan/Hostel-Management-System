package com.example.hms_fe1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hms_fe1.entity.FeePayment;

public interface FeePaymentRepository extends JpaRepository<FeePayment, String> {

}
