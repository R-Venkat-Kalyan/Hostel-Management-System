package com.example.hms_fe1.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.FeePayment;
import com.example.hms_fe1.entity.Room;
import com.example.hms_fe1.entity.Student;
import com.example.hms_fe1.repository.FeePaymentRepository;

@Service
public class FeePaymentService {
	
    @Autowired
    private FeePaymentRepository feePaymentRepository;
    
    @Autowired
    private StudentService studentService;

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
    
    public Set<FeePayment> getAllPaymentsByStu(String stuId){
    	List<FeePayment> allPayments = feePaymentRepository.findAll();
    	Set<FeePayment> stuPayments = new HashSet<FeePayment>();
    	for(FeePayment payment: allPayments) {
    		if(payment.getStudent().getStu_id().equals(stuId))
    			stuPayments.add(payment);
    	}
    	return stuPayments;
    }
    
    public int getLatestBalance(String stuId){
    	return feePaymentRepository.getLatestBalance(stuId);
    }
    
    public void addPayment(String stuId, int amountPaid, LocalDate nextDueDate, LocalDate paidDate) {
		// Fetch the student
		Student student = studentService.findStudentById(stuId);
//                .orElseThrow(() -> new RuntimeException("Student not found"));

		// Get the latest balance amount
		Integer latestBalance = getLatestBalance(stuId);

//        if (latestBalance == null) {
//            latestBalance = student.getTotalFee();  // If no payment exists, start with full fee
//        }

		// Calculate the new balance
		int newBalance = latestBalance - amountPaid;

		// Save new payment entry
		FeePayment payment = getFeeByStuId(stuId);
		payment.setStudent(student);
		payment.setFee_amount(payment.getFee_amount()); // Keep original fee for reference
		payment.setAmount_paid(amountPaid);
		payment.setBalance_amount(newBalance);
		payment.setLast_paid_date(paidDate);
		payment.setNext_due_date(nextDueDate);
		save(payment);
	
	}
}

