package com.example.hms_fe1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;

@Entity
public class FeePayment {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated ID for fee payment record
	private Integer fee_amount;
	private Integer amount_paid;
	private Integer balance_amount;
	private LocalDate last_paid_date;
	private LocalDate next_due_date;

	@OneToOne
	private Student student;

	public FeePayment() {

	}

	public FeePayment( Integer fee_amount, Integer amount_paid, Integer balance_amount,
			LocalDate last_paid_date, LocalDate next_due_date, Student student) {
		super();
		this.fee_amount = fee_amount;
		this.amount_paid = amount_paid;
		this.balance_amount = balance_amount;
		this.last_paid_date = last_paid_date;
		this.next_due_date = next_due_date;
		this.student = student;
	}

	public Integer getFee_amount() {
		return fee_amount;
	}

	public void setFee_amount(Integer fee_amount) {
		this.fee_amount = fee_amount;
	}

	public Integer getAmount_paid() {
		return amount_paid;
	}

	public void setAmount_paid(Integer amount_paid) {
		this.amount_paid = amount_paid;
	}

	public Integer getBalance_amount() {
		return balance_amount;
	}

	public void setBalance_amount(Integer balance_amount) {
		this.balance_amount = balance_amount;
	}

	public LocalDate getLast_paid_date() {
		return last_paid_date;
	}

	public void setLast_paid_date(LocalDate last_paid_date) {
		this.last_paid_date = last_paid_date;
	}

	public LocalDate getNext_due_date() {
		return next_due_date;
	}

	public void setNext_due_date(LocalDate next_due_date) {
		this.next_due_date = next_due_date;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
