package com.telegrambot.cyclebot.service;

import com.telegrambot.cyclebot.model.User;
import com.telegrambot.cyclebot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void registerUser(org.telegram.telegrambots.meta.api.objects.User telegramUser, Long chatId) {
        User user = userRepository.findById(chatId).orElse(new User());
        user.setChatId(chatId);
        user.setFirstName(telegramUser.getFirstName());
        user.setLastName(telegramUser.getLastName());
        user.setUsername(telegramUser.getUserName());

        userRepository.save(user);
        log.info("User registered: {} ({})", telegramUser.getFirstName(), chatId);
    }

    public void saveFeedback(Long chatId, String feedbackText) {
        log.info("Feedback from user {}: {}", chatId, feedbackText);


    }

    public User getUser(Long chatId) {
        return userRepository.findById(chatId).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
