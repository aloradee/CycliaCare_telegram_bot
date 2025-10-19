package com.telegrambot.cyclebot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class CycliaCareBot extends TelegramLongPollingCommandBot {

    private final String botUserName;

    public CycliaCareBot(
        @Value("${telegram.bot.token}") String botToken,
        @Value("${telegram.bot.username}") String botUserName,
        List<IBotCommand> commandList
    ) {
        super(botToken);
        this.botUserName = botUserName;

        commandList.forEach(this::register);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }

    @Override
    public String getBotUsername() {
        return "";
    }
}
