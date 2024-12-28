package com.example.hms_fe1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.FeePayment;
import com.example.hms_fe1.repository.FeePaymentRepository;

@Service
public class FeePaymentService {
	
    @Autowired
    private FeePaymentRepository feePaymentRepository;

    public void save(FeePayment feePayment) {
        feePaymentRepository.save(feePayment);
    }
}

