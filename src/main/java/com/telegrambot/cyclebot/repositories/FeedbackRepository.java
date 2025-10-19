package com.telegrambot.cyclebot.repositories;

import com.telegrambot.cyclebot.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProcessedFalseOrderByCreatedAtDesc();
    List<Feedback> findByUserChatIdOrderByCreatedAtDesc(Long chatId);
    Long countByProcessedFalse();
}