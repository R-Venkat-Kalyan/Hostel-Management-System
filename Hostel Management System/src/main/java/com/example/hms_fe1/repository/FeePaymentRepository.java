package com.example.hms_fe1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hms_fe1.entity.FeePayment;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
	
	 @Query(value = "SELECT fp.balance_amount FROM fee_payment fp WHERE fp.student_stu_id = :stuId ORDER BY fp.last_paid_date DESC LIMIT 1", nativeQuery = true)
	    Integer getLatestBalance(@Param("stuId") String stuId);

}
