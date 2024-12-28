package com.example.hms_fe1.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

//@Entity
//@Table(name = "mapping_details")
//public class MappingEntity {
//
//	private String stu_name;
//
//	@Id
//	private long stu_id;
//
//	private long stu_phone;
//
//	private String stu_mail;
//
//	private int room_number;
//
//	private String room_type;
//
//	private int fee_amount;
//
//	private int amount_paid;
//
//	private int balance_amount;
//
//	private LocalDate last_paid_date;
//
//	private LocalDate next_due_date;
//	
//	public MappingEntity() {
//
//	}
//
//	public MappingEntity(String stu_name, long stu_id, long stu_phone, String stu_mail, int room_number,
//			String room_type, int fee_amount, int amount_paid, int balance_amount, LocalDate last_paid_date,
//			LocalDate next_due_date) {
//		super();
//		this.stu_name = stu_name;
//		this.stu_id = stu_id;
//		this.stu_phone = stu_phone;
//		this.stu_mail = stu_mail;
//		this.room_number = room_number;
//		this.room_type = room_type;
//		this.fee_amount = fee_amount;
//		this.amount_paid = amount_paid;
//		this.balance_amount = balance_amount;
//		this.last_paid_date = last_paid_date;
//		this.next_due_date = next_due_date;
//	}
//
//
//	public String getStu_name() {
//		return stu_name;
//	}
//
//	public void setStu_name(String stu_name) {
//		this.stu_name = stu_name;
//	}
//
//	public long getStu_id() {
//		return stu_id;
//	}
//
//	public void setStu_id(long stu_id) {
//		this.stu_id = stu_id;
//	}
//
//	public long getStu_phone() {
//		return stu_phone;
//	}
//
//	public void setStu_phone(long stu_phone) {
//		this.stu_phone = stu_phone;
//	}
//
//	public String getStu_mail() {
//		return stu_mail;
//	}
//
//	public void setStu_mail(String stu_mail) {
//		this.stu_mail = stu_mail;
//	}
//
//	public int getRoom_number() {
//		return room_number;
//	}
//
//	public void setRoom_number(int room_number) {
//		this.room_number = room_number;
//	}
//
//	public String getRoom_type() {
//		return room_type;
//	}
//
//	public void setRoom_type(String room_type) {
//		this.room_type = room_type;
//	}
//
//	public int getFee_amount() {
//		return fee_amount;
//	}
//
//	public void setFee_amount(int fee_amount) {
//		this.fee_amount = fee_amount;
//	}
//
//	public int getAmount_paid() {
//		return amount_paid;
//	}
//
//	public void setAmount_paid(int amount_paid) {
//		this.amount_paid = amount_paid;
//	}
//
//	public int getBalance_amount() {
//		return balance_amount;
//	}
//
//	public void setBalance_amount(int balance_amount) {
//		this.balance_amount = balance_amount;
//	}
//
//	public LocalDate getLast_paid_date() {
//		return last_paid_date;
//	}
//
//	public void setLast_paid_date(LocalDate last_paid_date) {
//		this.last_paid_date = last_paid_date;
//	}
//
//	public LocalDate getNext_due_date() {
//		return next_due_date;
//	}
//
//	public void setNext_due_date(LocalDate next_due_date) {
//		this.next_due_date = next_due_date;
//	}
//
//}


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


