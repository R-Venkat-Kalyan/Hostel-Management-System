package com.example.hms_fe1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.FeePayment;
import com.example.hms_fe1.entity.Room;
import com.example.hms_fe1.repository.FeePaymentRepository;

@Service
public class FeePaymentService {
	
    @Autowired
    private FeePaymentRepository feePaymentRepository;

    public void save(FeePayment feePayment) {
        feePaymentRepository.save(feePayment);
    }
    
    public FeePayment getFeeByStuId(String stu_id) {
    	for(FeePayment feePayment: feePaymentRepository.findAll()) {
    		if(feePayment.getStudent().getStu_id().equals(stu_id))
    			return feePayment;
    	}
    	return null;
    }
}

