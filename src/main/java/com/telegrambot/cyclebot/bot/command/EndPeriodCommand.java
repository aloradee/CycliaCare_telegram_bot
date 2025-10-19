package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.service.CycleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.telegrambot.cyclebot.utils.TelegramMessageSender.sendMessage;

@Service
@AllArgsConstructor
@Slf4j
public class EndPeriodCommand implements IBotCommand {

    private final CycleService cycleService;

    @Override
    public String getCommandIdentifier() {
        return "endperiod";
    }

    @Override
    public String getDescription() {
        return "Отметить конец месячных (формат: /endperiod [дата])";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long chatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);

        try {
            LocalDate endDate;
            if (arguments.length == 0) {
                endDate = LocalDate.now();
            } else {
                endDate = parseDate(arguments[0]);
            }

            String result = cycleService.markPeriodEnd(chatId, endDate);
            answer.setText(result);

        } catch (DateTimeParseException e) {
            answer.setText("""
                    ❌ Неверный формат даты!
                    
                    📅 Используйте: /endperiod ДД.ММ.ГГГГ
                    
                    💡 Примеры:
                    /endperiod 05.10.2024
                    /endperiod (использует текущую дату)""");
        } catch (Exception e) {
            log.error("Error ending period", e);
            answer.setText("❌ Ошибка при отметке конца месячных.");
        }

        sendMessage(answer, absSender);
    }

    private LocalDate parseDate(String dateStr) throws DateTimeParseException {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}