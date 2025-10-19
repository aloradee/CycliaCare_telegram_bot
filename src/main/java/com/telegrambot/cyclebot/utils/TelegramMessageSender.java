package com.telegrambot.cyclebot.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class TelegramMessageSender {

    public static void sendMessage(SendMessage message, AbsSender absSender) {
        try {
            absSender.execute(message);
            log.debug("Message sent successfully to chat: {}", message.getChatId());
        } catch (TelegramApiException e) {
            log.error("Error sending message to chat {}: {}", message.getChatId(), e.getMessage());

            // Повторная попытка при сетевых ошибках
            if (e.getMessage().contains("Connection reset") ||
                    e.getMessage().contains("Timeout") ||
                    e.getMessage().contains("Unable to execute")) {

                log.info("Retrying to send message to chat: {}", message.getChatId());
                retrySendMessage(message, absSender);
            }
        }
    }

    private static void retrySendMessage(SendMessage message, AbsSender absSender) {
        try {
            Thread.sleep(1000); // Ждем 1 секунду перед повторной попыткой
            absSender.execute(message);
            log.info("Message sent successfully on retry to chat: {}", message.getChatId());
        } catch (Exception e) {
            log.error("Failed to send message on retry to chat {}: {}", message.getChatId(), e.getMessage());
        }
    }
}