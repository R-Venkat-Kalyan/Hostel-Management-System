package com.example.hms_fe1.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.hms_fe1.entity.FeeReceipt;
import com.example.hms_fe1.repository.FeeReceiptRepository;

@Service
public class FeeReceiptService {

	@Autowired
	private FeeReceiptRepository feeReceiptRepository;

	public void saveFeeReceipt(String studentId, String date, String paymentAmount, MultipartFile receiptImage)
			throws IOException {
		FeeReceipt feeReceipt = new FeeReceipt();
		feeReceipt.setStudentId(studentId);
		feeReceipt.setDate(date);
		feeReceipt.setPaymentAmount(paymentAmount);
		feeReceipt.setReceiptImage(receiptImage.getBytes());
		feeReceipt.setFileName(receiptImage.getOriginalFilename());
		feeReceiptRepository.save(feeReceipt);
	}
	
	public Set<FeeReceipt> findStuReceipts(String studentId) {
		List<FeeReceipt> allReceipts = feeReceiptRepository.findAll();
		Set<FeeReceipt> stuReceipts = new HashSet<FeeReceipt>();
		for(FeeReceipt receipt: allReceipts) {
			if(receipt.getStudentId().equals(studentId))
				stuReceipts.add(receipt);
		}
		return stuReceipts;
	}
	
	public Optional<FeeReceipt> findById(long id) {
	    return feeReceiptRepository.findById(id);
	}

}
