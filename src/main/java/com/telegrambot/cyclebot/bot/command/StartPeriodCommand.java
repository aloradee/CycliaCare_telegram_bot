package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.service.CycleService;
import com.telegrambot.cyclebot.service.UserService;
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
public class StartPeriodCommand implements IBotCommand {

    private final CycleService cycleService;
    private final UserService userService;
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
            // Сначала убедимся, что пользователь зарегистрирован
            userService.registerUser(message.getFrom(), chatId);

            LocalDate startDate;
            if (arguments.length == 0) {
                startDate = LocalDate.now();
            } else {
                startDate = parseDate(arguments[0]);
            }

            String result = cycleService.startNewCycle(chatId, startDate);
            answer.setText(result);

        } catch (DateTimeParseException e) {
            answer.setText("""
                ❌ Неверный формат даты. Используйте: /startperiod ДД.ММ.ГГГГ
                💡 Пример: /startperiod 15.12.2024""");
        } catch (Exception e) {
            log.error("Error starting period for user {}", chatId, e);
            answer.setText("❌ Ошибка при создании цикла. Попробуйте еще раз или используйте /help");
        }

        sendMessage(answer, absSender);
    }

    private LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
