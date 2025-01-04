package com.example.hms_fe1.service;

public class StudentDetailsDTO {
	
	private String studentId;
	private String studentName;
	private String roomNumber;
	private String roomType;
	private int feePaid;
	private int feePending;
	private String phone;

	
	public StudentDetailsDTO() {
		
	}

	public StudentDetailsDTO(String studentId, String studentName, String roomNumber, String roomType, int feePaid,
			int feePending, String phone) {
		this.studentId = studentId;
		this.studentName = studentName;
		this.roomNumber = roomNumber;
		this.roomType = roomType;
		this.feePaid = feePaid;
		this.feePending = feePending;
		this.phone = phone;
	}

// Getters and Setters
	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public int getFeePaid() {
		return feePaid;
	}

	public void setFeePaid(int feePaid) {
		this.feePaid = feePaid;
	}

	public int getFeePending() {
		return feePending;
	}

	public void setFeePending(int feePending) {
		this.feePending = feePending;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}