package com.telegrambot.cyclebot.service;

import com.telegrambot.cyclebot.model.Feedback;
import com.telegrambot.cyclebot.model.User;
import com.telegrambot.cyclebot.repositories.FeedbackRepository;
import com.telegrambot.cyclebot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    /**
     * Сохранить отзыв от пользователя
     */
    public void saveFeedback(Long chatId, String message, Feedback.FeedbackType type, Integer rating) {
        try {
            User user = userRepository.findById(chatId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Feedback feedback = new Feedback();
            feedback.setUser(user);
            feedback.setMessage(message);
            feedback.setType(type != null ? type : Feedback.FeedbackType.GENERAL_FEEDBACK);
            feedback.setRating(rating);

            feedbackRepository.save(feedback);

            log.info("📝 Feedback saved from user {}: {}", chatId,
                    message.length() > 50 ? message.substring(0, 50) + "..." : message);

        } catch (Exception e) {
            log.error("Error saving feedback from user {}", chatId, e);
        }
    }

    /**
     * Сохранить отзыв без указания типа (по умолчанию GENERAL_FEEDBACK)
     */
    public void saveFeedback(Long chatId, String message) {
        saveFeedback(chatId, message, Feedback.FeedbackType.GENERAL_FEEDBACK, null);
    }

    /**
     * Получить все необработанные отзывы
     */
    public List<Feedback> getUnprocessedFeedback() {
        return feedbackRepository.findByProcessedFalseOrderByCreatedAtDesc();
    }

    /**
     * Получить отзывы конкретного пользователя
     */
    public List<Feedback> getUserFeedback(Long chatId) {
        return feedbackRepository.findByUserChatIdOrderByCreatedAtDesc(chatId);
    }

    /**
     * Отметить отзыв как обработанный
     */
    public void markAsProcessed(Long feedbackId) {
        feedbackRepository.findById(feedbackId).ifPresent(feedback -> {
            feedback.setProcessed(true);
            feedbackRepository.save(feedback);
            log.info("✅ Feedback {} marked as processed", feedbackId);
        });
    }

    /**
     * Получить количество необработанных отзывов
     */
    public Long getUnprocessedCount() {
        return feedbackRepository.countByProcessedFalse();
    }
}