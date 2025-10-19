package com.telegrambot.cyclebot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final List<org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand> commands;

    public TelegramBot(List<org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand> commands) {
        super();
        this.commands = commands;
    }

    @PostConstruct
    public void init() {
        // Регистрируем все команды
        commands.forEach(this::register);

        // Регистрируем бота
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            log.info("Telegram bot registered successfully!");
        } catch (TelegramApiException e) {
            log.error("Error registering bot", e);
        }

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
    public String getBotToken() {
        return botToken;
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