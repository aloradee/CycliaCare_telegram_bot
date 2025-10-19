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
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText("""
                📋 Доступные команды:
                
                🔹 /start - Запуск бота
                🔹 /startperiod [дата] - Начать цикл (формат: ДД.ММ.ГГГГ)
                🔹 /status - Текущий статус цикла
                🔹 /symptom - Добавить симптомы
                🔹 /calendar - Календарь событий
                🔹 /history - История циклов
                🔹 /feedback - Отправить отзыв
                🔹 /help - Эта справка
                
                💡 Примеры использования:
                /startperiod 15.12.2024
                /startperiod (использует текущую дату)
                /symptom головная_боль средняя
                /feedback Отличный бот!
                
                ❓ По всем вопросам используйте /feedback""");

        sendMessage(answer, absSender);
    }
}
