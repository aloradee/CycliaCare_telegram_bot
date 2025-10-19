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

    /**
     * Регистрирует или обновляет пользователя
     */
    public User registerUser(org.telegram.telegrambots.meta.api.objects.User telegramUser, Long chatId) {
        User user = userRepository.findById(chatId).orElse(new User());
        user.setChatId(chatId);
        user.setFirstName(telegramUser.getFirstName());
        user.setLastName(telegramUser.getLastName());
        user.setUsername(telegramUser.getUserName());

        User savedUser = userRepository.save(user);
        log.info("User registered: {} ({})", telegramUser.getFirstName(), chatId);
        return savedUser;
    }

    /**
     * Сохраняет пользователя (для обновления данных)
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Получает пользователя, если не найден - создает нового
     */
    public User getUser(Long chatId) {
        return userRepository.findById(chatId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setChatId(chatId);
                    return userRepository.save(newUser);
                });
    }

    public void saveFeedback(Long chatId, String feedbackText) {
        log.info("Feedback from user {}: {}", chatId, feedbackText);
    }
}