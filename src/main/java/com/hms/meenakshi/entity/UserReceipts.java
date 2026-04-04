package com.hms.meenakshi.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "user_receipts")
public class UserReceipts {

    @Id
    private String id;
    private String userId;
    private LocalDate paymentDate;
    private Double amount;
    private String receiptUrl; // Cloudinary URL
    private String publicId;
}
