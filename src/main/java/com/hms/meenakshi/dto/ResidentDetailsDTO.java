package com.hms.meenakshi.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ResidentDetailsDTO {

    private String userId;
    private String name;           // Maps to Name
    private String universityId;    // Maps to University ID
    private String room;           // Maps to Room (Number)
    private String roomType;       // Maps to Room Type
    private double totalPayment;    // Maps to Total Payment
    private double duePayment;      // Maps to Due Payment
    private LocalDate lastPaidDate; // Maps to Last Paid Date
}
