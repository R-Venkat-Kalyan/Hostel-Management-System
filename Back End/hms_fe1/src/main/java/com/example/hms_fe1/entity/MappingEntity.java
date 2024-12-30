package com.example.hms_fe1.entity;

import java.time.LocalDate;

public class MappingEntity {

	private String stu_id;
	private String stu_name;
	private String stu_phone;
	private String room_number;
	private String room_type;
	private Integer fee_amount;
	private Integer amount_paid;
	private LocalDate last_paid_date;
	private LocalDate next_due_date;

	// Getters and Setters
	public String getStu_id() {
		return stu_id;
	}

	public void setStu_id(String stu_id) {
		this.stu_id = stu_id;
	}

	public String getStu_name() {
		return stu_name;
	}

	public void setStu_name(String stu_name) {
		this.stu_name = stu_name;
	}

	public String getStu_phone() {
		return stu_phone;
	}

	public void setStu_phone(String stu_phone) {
		this.stu_phone = stu_phone;
	}

	public String getRoom_number() {
		return room_number;
	}

	public void setRoom_number(String room_number) {
		this.room_number = room_number;
	}

	public String getRoom_type() {
		return room_type;
	}

	public void setRoom_type(String room_type) {
		this.room_type = room_type;
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
}
