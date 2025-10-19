package com.telegrambot.cyclebot.bot.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.telegrambot.cyclebot.utils.TelegramMessageSender.sendMessage;

@Service
@AllArgsConstructor
@Slf4j
public class HelpCommand implements IBotCommand {

    @Override
    public String getCommandIdentifier() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Помощь по командам бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText("""
            📋 Доступные команды:
            
            🔹 Основные:
            /start - Запуск бота
            /startperiod [дата] - Начать цикл
            /endperiod [дата] - Отметить конец месячных
            /changeperiod [дата] - Изменить дату начала
            
            🔹 Настройки:
            /setcycle [дни] - Установить длину цикла
            /status - Текущий статус
            
            🔹 Дополнительные:
            /symptom - Добавить симптомы
            /calendar - Календарь событий
            /history - История циклов
            /feedback - Отправить отзыв
            /help - Эта справка
            
            💡 Примеры:
            /startperiod 15.10.2024
            /endperiod 20.10.2024
            /setcycle 30
            /changeperiod 18.10.2024""");

        sendMessage(answer, absSender);
    }
}
