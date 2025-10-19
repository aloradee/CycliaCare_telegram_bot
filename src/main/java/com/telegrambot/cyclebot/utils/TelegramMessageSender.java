package com.telegrambot.cyclebot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramMessageSender {
    public static void sendMessage(SendMessage answer, AbsSender absSender) {
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения пользователю {}", answer.getChatId(), e);
        }
    }
}

