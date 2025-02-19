package com.example.hms_fe1.entity;

import java.time.LocalDate;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fee_payment_history")
public class FeePaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stuId;
    private int amountPaid;
    private LocalDate  lastPaidDate;
    private LocalDate nextDueDate;

    @Column(nullable = false)
    private Boolean isVerified = false;

    
	public FeePaymentHistory() {
		
	}

	public FeePaymentHistory(Long id, Long stuId, int amountPaid, LocalDate lastPaidDate, LocalDate nextDueDate,
			Boolean isVerified) {
		this.id = id;
		this.stuId = stuId;
		this.amountPaid = amountPaid;
		this.lastPaidDate = lastPaidDate;
		this.nextDueDate = nextDueDate;
		this.isVerified = isVerified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStuId() {
		return stuId;
	}

	public void setStuId(Long stuId) {
		this.stuId = stuId;
	}

	public int getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(int amountPaid) {
		this.amountPaid = amountPaid;
	}

	public LocalDate getLastPaidDate() {
		return lastPaidDate;
	}

	public void setLastPaidDate(LocalDate lastPaidDate) {
		this.lastPaidDate = lastPaidDate;
	}

	public LocalDate getNextDueDate() {
		return nextDueDate;
	}

	public void setNextDueDate(LocalDate nextDueDate) {
		this.nextDueDate = nextDueDate;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

    
}
