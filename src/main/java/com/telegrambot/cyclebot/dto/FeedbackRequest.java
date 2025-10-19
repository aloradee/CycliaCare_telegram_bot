package com.telegrambot.cyclebot.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackRequest {
    private Long chatId;
    private String userFirstName;
    private String userLastName;
    private String username;
    private String message;
    private LocalDateTime createdAt;
    private FeedbackType type;
    private Integer rating; // 1-5 stars, optional

    public enum FeedbackType {
        BUG_REPORT,      // Сообщение об ошибке
        FEATURE_REQUEST, // Запрос функции
        GENERAL_FEEDBACK, // Общий отзыв
        QUESTION,        // Вопрос
        COMPLAINT        // Жалоба
    }
}