package com.example.hms_fe1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.FeePaymentHistory;
import com.example.hms_fe1.repository.FeePaymentHistoryRepository;

@Service
public class FeePaymentHistoryService {
	
	@Autowired
	private FeePaymentHistoryRepository feePaymentHistoryRepository;
	
	public void savePayment(FeePaymentHistory feePayment) {
		feePaymentHistoryRepository.save(feePayment);
	}
	
	public List<FeePaymentHistory> unVerifiedPayments(){
		return feePaymentHistoryRepository.approvalPendingPayments();
	}

}
