package com.example.hms_fe1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Student {

	@Id
	private String stu_id;
	private String stu_name;
	private String stu_phone;
	private String stu_mail;

	@OneToOne(mappedBy = "student")
	private Room room;

	@OneToOne(mappedBy = "student")
	private FeePayment feePayment;

	public Student() {

	}

	public Student(String stu_id, String stu_name, String stu_phone, String stu_mail, Room room,
			FeePayment feePayment) {
		super();
		this.stu_id = stu_id;
		this.stu_name = stu_name;
		this.stu_phone = stu_phone;
		this.stu_mail = stu_mail;
		this.room = room;
		this.feePayment = feePayment;
	}

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

	public String getStu_mail() {
		return stu_mail;
	}

	public void setStu_mail(String stu_mail) {
		this.stu_mail = stu_mail;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public FeePayment getFeePayment() {
		return feePayment;
	}

	public void setFeePayment(FeePayment feePayment) {
		this.feePayment = feePayment;
	}
}
