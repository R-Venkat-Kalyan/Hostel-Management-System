package com.hms.meenakshi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentHistoryDTO {

    private String id;
    private String userName;
    private String roomNumber;
    private double amount;
    private String paymentMode;
    private String transactionRef;
    private LocalDateTime paidDate;
    private String status; // PENDING, APPROVED, REJECTED
}
