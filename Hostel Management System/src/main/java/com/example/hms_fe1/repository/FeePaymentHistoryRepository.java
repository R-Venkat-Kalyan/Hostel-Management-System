package com.example.hms_fe1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.hms_fe1.entity.FeePaymentHistory;

@Repository
public interface FeePaymentHistoryRepository extends JpaRepository<FeePaymentHistory, Long> {
	
	@Query("SELECT f FROM FeePaymentHistory f WHERE f.isVerified = false ORDER BY f.lastPaidDate DESC, f.stuId ASC")
	List<FeePaymentHistory> approvalPendingPayments();


}
