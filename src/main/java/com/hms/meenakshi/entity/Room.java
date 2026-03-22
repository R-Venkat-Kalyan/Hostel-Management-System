package com.hms.meenakshi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    private String id;

    private String roomNumber;    // e.g., "201-A"
    private String roomFloor;     // "1st Floor", "2nd Floor", etc.
    private String roomType;      // "4 SHARING AC", etc.
    private Double rentAmount;    // 75000, 85000, 95000
    private Integer capacity;     // 2, 3, 4
    private Integer occupiedCount = 0;
    private String status;        // AVAILABLE, MAINTENANCE, FULL

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

