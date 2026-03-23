package com.hms.meenakshi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "roomAssignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomAssignment {

    @Id
    private String id;

    private String userId;      // The Resident's ID
    private String roomId;      // The Room's ID

    private LocalDate startDate;   // Format: YYYY-MM-DD
    private LocalDate endDate;     // Null if currently staying
    private String status;      // "ACTIVE" or "COMPLETED"

    private LocalDate createdAt;
    private LocalDate updatedAt;
}
