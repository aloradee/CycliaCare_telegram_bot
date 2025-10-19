package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.model.User;
import com.telegrambot.cyclebot.repositories.UserRepository;
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
import java.time.temporal.ChronoUnit;

import static com.telegrambot.cyclebot.utils.TelegramMessageSender.sendMessage;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarCommand implements IBotCommand {

    private final CycleService cycleService;
    private final UserRepository userRepository;

    @Override
    public String getCommandIdentifier() {
        return "calendar";
    }

    @Override
    public String getDescription() {
        return "Календарь предстоящих событий";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        String calendar = cycleService.getCalendar(message.getChatId());

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText(calendar);

        sendMessage(answer, absSender);
    }

    // И добавим метод в CycleService.java:
    public String getCalendar(Long chatId) {
        try {
            User user = userRepository.findById(chatId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getLastPeriodStart() == null) {
                return "❓ Сначала начните отслеживание цикла с помощью /startperiod";
            }

            long daysUntilNext = ChronoUnit.DAYS.between(LocalDate.now(), user.getNextPeriodStart());

            return String.format("""
                📅 Календарь событий:
                
                🩸 Последняя менструация: %s
                🩸 Следующая менструация: %s (%d дней)
                🎯 Овуляция: %s
                📏 Длина цикла: %d дней
                
                💡 Используйте /status для детального статуса""",
                    user.getLastPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getNextPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    daysUntilNext,
                    user.getOvulationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getCycleLength());

        } catch (Exception e) {
            return "❓ Сначала начните отслеживание цикла с помощью /startperiod";
        }
    }
}
