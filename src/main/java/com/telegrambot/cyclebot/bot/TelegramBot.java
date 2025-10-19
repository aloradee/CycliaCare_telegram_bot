package com.telegrambot.cyclebot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {

    private final String botUsername;
    private final List<org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand> commands;

    public TelegramBot(
            String botToken,
            String botUsername,
            List<org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand> commands
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.commands = commands;

        // Регистрируем все команды
        commands.forEach(this::register);

        // Обработка некорректных команд
        registerDefaultAction((absSender, message) -> {
            SendMessage text = new SendMessage();
            text.setChatId(message.getChatId());
            text.setText("❌ Неизвестная команда. Используйте /help для списка команд.");
            try {
                absSender.execute(text);
            } catch (TelegramApiException e) {
                log.error("Error sending message", e);
            }
        });
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText().toLowerCase();

            if (text.contains("привет") || text.contains("hello")) {
                SendMessage answer = new SendMessage();
                answer.setChatId(message.getChatId());
                answer.setText("👋 Привет! Используйте /start для начала работы с ботом.");
                try {
                    execute(answer);
                } catch (TelegramApiException e) {
                    log.error("Error sending message", e);
                }
            }
        }
    }
}