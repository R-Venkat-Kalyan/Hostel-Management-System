package com.hms.meenakshi.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "app_Feedback")
@Data
public class FeedBack {

    @Id
    private String id;
    private String userId;      // Who sent it
    private String userName;    // Name for quick view
    private String userRole;    // RESIDENT, MANAGER, or OWNER

    private Integer rating;     // 1 to 5 (mapped to emojis)
    private String category;    // UI/UX, Speed, Features, Security
    private String likes;       // What they like
    private String improvements;// What they want to change

    private LocalDateTime submittedAt = LocalDateTime.now();
}
