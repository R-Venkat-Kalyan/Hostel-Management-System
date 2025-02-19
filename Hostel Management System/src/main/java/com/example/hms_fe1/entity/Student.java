package com.example.hms_fe1.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FeePayment> feePayments = new ArrayList<>();

	public Student() {

	}

	public Student(String stu_id, String stu_name, String stu_phone, String stu_mail, Room room,
			List<FeePayment> feePayments) {
		super();
		this.stu_id = stu_id;
		this.stu_name = stu_name;
		this.stu_phone = stu_phone;
		this.stu_mail = stu_mail;
		this.room = room;
		this.feePayments = feePayments;
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

	public List<FeePayment> getFeePayment() {
		return feePayments;
	}

	public void setFeePayment(List<FeePayment> feePayments) {
		this.feePayments = feePayments;
	}
}
