package com.example.hms_fe1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "FeeReceipts")
public class FeeReceipt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String studentId;
	private String date;
	private String paymentAmount;

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] receiptImage;

	private String fileName;

	public FeeReceipt() {

	}

	public FeeReceipt(long id, String studentId, String date, String paymentAmount, byte[] receiptImage,
			String fileName) {
		super();
		this.id = id;
		this.studentId = studentId;
		this.date = date;
		this.paymentAmount = paymentAmount;
		this.receiptImage = receiptImage;
		this.fileName = fileName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public byte[] getReceiptImage() {
		return receiptImage;
	}

	public void setReceiptImage(byte[] receiptImage) {
		this.receiptImage = receiptImage;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
