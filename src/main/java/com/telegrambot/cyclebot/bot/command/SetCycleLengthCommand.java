package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.service.CycleService;
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
public class SetCycleLengthCommand implements IBotCommand {

    private final CycleService cycleService;

    @Override
    public String getCommandIdentifier() {
        return "setcycle";
    }

    @Override
    public String getDescription() {
        return "Установить длину цикла (формат: /setcycle [дней])";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long chatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);

        try {
            if (arguments.length == 0) {
                answer.setText("""
                        📏 Чтобы установить длину цикла, используйте:
                        
                        /setcycle [количество дней]
                        
                        💡 Примеры:
                        /setcycle 28 - стандартный цикл
                        /setcycle 30 - более длинный цикл
                        /setcycle 25 - более короткий цикл
                        
                        🎯 Овуляция будет рассчитываться автоматически (цикл - 14 дней)""");
            } else {
                int cycleLength = Integer.parseInt(arguments[0]);
                String result = cycleService.setCycleLength(chatId, cycleLength);
                answer.setText(result);
            }
        } catch (NumberFormatException e) {
            answer.setText("""
                    ❌ Неверный формат числа!
                    
                    📏 Используйте целое число от 21 до 35 дней.
                    
                    💡 Пример: /setcycle 28""");
        } catch (Exception e) {
            log.error("Error setting cycle length", e);
            answer.setText("❌ Ошибка при установке длины цикла.");
        }

        sendMessage(answer, absSender);
    }
}