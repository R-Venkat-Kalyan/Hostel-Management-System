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
    
	       
//    public void addPayment(String stuId, int amountPaid, LocalDate nextDueDate, LocalDate paidDate) {
//        // Fetch the student
//        Student student = studentService.findStudentById(stuId);
//        
//        // Fetch all previous payments for this student
//        Set<FeePayment> payments = getAllPaymentsByStu(stuId);
//
//        // Get the total amount already paid
//        int totalPaidSoFar = payments.stream().mapToInt(FeePayment::getAmount_paid).sum();
//
//        // Calculate updated total paid amount
//        int updatedTotalPaid = totalPaidSoFar + amountPaid;
//
//        // Fetch total fee amount
//        int totalFee = getFeeByStuId(stuId).getFee_amount();
//
//        // Ensure that the total paid does not exceed the total fee
//        if (updatedTotalPaid > totalFee) {
//            throw new RuntimeException("Payment exceeds total fee amount!");
//        }
//
//        // Calculate new balance
//        int newBalance = totalFee - updatedTotalPaid;
//
//        // Create a new FeePayment entry
//        FeePayment newPayment = new FeePayment();
//        newPayment.setStudent(student);
//        newPayment.setFee_amount(totalFee); // Keep total fee reference
//        newPayment.setAmount_paid(amountPaid); // Store only the current transaction amount
//        newPayment.setBalance_amount(newBalance); // Updated balance amount
//        newPayment.setLast_paid_date(paidDate);
//        newPayment.setNext_due_date(nextDueDate);
//
//        // Save the new payment entry
//        feePaymentRepository.save(newPayment);
//    }
//    
   


}

