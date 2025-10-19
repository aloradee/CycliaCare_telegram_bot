package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.service.CycleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.telegrambot.cyclebot.utils.TelegramMessageSender.sendMessage;

@Service
@AllArgsConstructor
@Slf4j
public class StartPeriodCommand implements IBotCommand {

    private final CycleService cycleService;

    @Override
    public String getCommandIdentifier() {
        return "startperiod";
    }

    @Override
    public String getDescription() {
        return "Начать отсчет цикла (формат: /startperiod ДД.ММ.ГГГГ)";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long chatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);

        try {
            LocalDate startDate;
            if (arguments.length == 0) {
                startDate = LocalDate.now();
            } else {
                startDate = parseDate(arguments[0]);
            }

            String result = cycleService.startNewCycle(chatId, startDate);
            answer.setText(result);
        } catch (DateTimeParseException e) {
            answer.setText("❌ Неверный формат даты. Используйте: /startperiod ДД.ММ.ГГГГ\nПример: /startperiod 15.12.2024");
        } catch (Exception e) {
            log.error("Error starting period", e);
            answer.setText("❌ Ошибка при сохранении данных.");
        }

        sendMessage(answer, absSender);
    }

    private LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
