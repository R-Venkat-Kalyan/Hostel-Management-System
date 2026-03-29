package com.hms.meenakshi.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payments")
@Data
public class Payments {

    @Id
    private String id;
    private String userId;
    private double amount;        // Changed to double for math
    private String paymentMode;   // CASH, UPI, TRANSFER
    private String transactionRef;
    private LocalDateTime paidDate;

    private String status;        // PENDING, APPROVED, REJECTED
    private String createdBy;     // Manager ID
    private String approvedBy;
    private LocalDateTime approvedDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
