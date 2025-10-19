package com.telegrambot.cyclebot.config;

import com.telegrambot.cyclebot.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;

import java.util.List;

@Configuration
public class BotConfig {

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.name}")
    private String botName;

    @Bean
    public TelegramBot telegramBot(List<IBotCommand> commands) {
        return new TelegramBot(botToken, botName, commands);
    }
}