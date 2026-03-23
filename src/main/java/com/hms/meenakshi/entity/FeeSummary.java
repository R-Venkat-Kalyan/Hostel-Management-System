package com.hms.meenakshi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "feeSummaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeSummary {

    @Id
    private String id;

    private String userId;        // Link to Resident

    private double totalAmount;   // Total rent/fees charged so far
    private double paidAmount;    // Total actually paid
    private double pendingAmount; // totalAmount - paidAmount

    private LocalDate lastPaymentDate;
    private LocalDate nextDueDate;
    private String status;        // "PAID", "PARTIAL", "UNPAID"

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;


}
