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
public class ChangeStartDateCommand implements IBotCommand {

    private final CycleService cycleService;

    @Override
    public String getCommandIdentifier() {
        return "changeperiod";
    }

    @Override
    public String getDescription() {
        return "Изменить дату начала цикла (формат: /changeperiod ДД.ММ.ГГГГ)";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {

        Long chatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);

        try {
            if(strings.length == 0) {
                answer.setText("""
                        🔄 Чтобы изменить дату начала цикла, используйте:
                        
                        /changeperiod ДД.ММ.ГГГГ
                        
                        📝 Примеры:
                        /changeperiod 15.10.2024
                        /changeperiod 01.11.2024
                        
                        💡 Текущую дату можно посмотреть с помощью /status""");
            } else {
                LocalDate newStartDate = parseDate(strings[0]);
                String result = cycleService.changeCycleStartDate(chatId, newStartDate);
                answer.setText(result);
            }
        } catch (DateTimeParseException e) {
            answer.setText("""
                    ❌ Неверный формат даты!
                    
                    📅 Используйте формат: ДД.ММ.ГГГГ
                    
                    💡 Примеры:
                    /changeperiod 15.10.2024
                    /changeperiod 01.11.2024""");
        } catch (Exception e) {
            log.error("Error changing period start date", e);
            answer.setText("❌ Ошибка при изменении даты. Сначала используйте /startperiod чтобы начать отслеживание.");
        }

        sendMessage(answer, absSender);
    }
    private LocalDate parseDate(String dateStr) throws DateTimeParseException {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
