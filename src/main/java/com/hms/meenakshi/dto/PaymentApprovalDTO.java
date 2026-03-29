package com.hms.meenakshi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentApprovalDTO {

    private String id;
    private String userName;
    private String roomNumber;
    private double dueAmount;
    private double amount;
    private String paymentMode;
    private String transactionRef;
    private LocalDateTime paidDate;
    private String managerName;
}
