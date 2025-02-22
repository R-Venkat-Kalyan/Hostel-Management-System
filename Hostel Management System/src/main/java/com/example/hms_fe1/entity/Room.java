package com.example.hms_fe1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Room {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String room_number;
	private String room_type;
	private int capacity = 9; // Default capacity

	@OneToOne
	private Student student;

	// Method to decrement capacity
	public void decrementCapacity() {
		if (capacity > 0) {
			capacity--;
		}
	}

	public Room() {

	}

	public Room(String room_number, String room_type, int capacity, Student student) {
		super();
		this.room_number = room_number;
		this.room_type = room_type;
		this.capacity = capacity;
		this.student = student;
	}

	// Getters and Setters
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
