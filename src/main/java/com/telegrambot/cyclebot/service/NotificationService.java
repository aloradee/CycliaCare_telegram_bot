package com.telegrambot.cyclebot.service;

import com.telegrambot.cyclebot.bot.TelegramBot;
import com.telegrambot.cyclebot.model.User;
import com.telegrambot.cyclebot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final TelegramBot telegramBot;

    @Scheduled(cron = "0 9 * * * *")
    public void sendDailyReminders() {
        LocalDate today = LocalDate.now();
        List<User> users = userRepository.findAll();

        for(User user : users) {
            if(user.getNextPeriodStart() != null) {
                checkAndSendReminders(user, today);
            }
        }
    }

    public void checkAndSendReminders(User user, LocalDate today) {
        try {
            if (today.equals(user.getNextPeriodStart().minusDays(1))) {
                sendMessage(user.getChatId(), "🔔 Напоминание: менструация ожидается завтра!");
            }
            if (today.equals(user.getOvulationDate())) {
                sendMessage(user.getChatId(), "🎯 Напоминание: сегодня овуляция!");
            }
            if (today.equals(user.getNextPeriodStart())) {
                sendMessage(user.getChatId(), "🩸 Напоминание: сегодня начало менструации!");
            }
        } catch (Exception e) {
            log.error("Error sending reminder to user {}", user.getChatId(), e);
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to {}", chatId, e);
        }
    }
}
