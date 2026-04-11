package com.hms.meenakshi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "vacated_residents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacatedResident {
    @Id
    private String id;
    private String name;
    private String universityId;
    private String roomNumber;
    private LocalDate joinedDate;
    private LocalDate vacatedDate;
    private double totalPaidDuringStay;
    private String reason; // "PERMANENT_EXIT" or "ROOM_SWITCH"
}
