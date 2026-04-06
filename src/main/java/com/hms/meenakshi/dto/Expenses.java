package com.hms.meenakshi.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "Expenses")
@Data
public class Expenses {

    @Id
    private String id;

    private String title;
    private String category;
    private double amount;
    private LocalDate expenseDate;
    private String notes;

    // Audit Fields
    private String addedBy;     // Name or ID of the person (e.g., "Bannu")
    private String addedByRole; // Role (e.g., "MANAGER" or "OWNER")
    private LocalDateTime createdAt = LocalDateTime.now();
}
