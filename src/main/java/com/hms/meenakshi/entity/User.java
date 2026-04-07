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

@Document(collection = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    // Core Authentication & Security
    private String email;
    private String password;
    private String role;           // Hidden input name="role" (e.g., "RESIDENT")
    private String status;         // Hidden input name="status" (e.g., "ACTIVE")

    // Resident-specific fields (Matching UI)
    private String fullName;
    private String universityId; // acts as security pin for manager
    private String phone;

    // Relationships & Tracking, NA for Manager Role
    private String roomId;         // Set during room assignment logic
    private String feeSummaryId;   // FK to feeSummaries

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;
}
