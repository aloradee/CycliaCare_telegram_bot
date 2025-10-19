package com.telegrambot.cyclebot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_chat_id")
    private User user;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private FeedbackType type;

    private Integer rating; // 1-5 stars

    private LocalDateTime createdAt;
    private Boolean processed = false; // Обработано ли администратором

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum FeedbackType {
        BUG_REPORT,
        FEATURE_REQUEST,
        GENERAL_FEEDBACK,
        QUESTION,
        COMPLAINT
    }
}