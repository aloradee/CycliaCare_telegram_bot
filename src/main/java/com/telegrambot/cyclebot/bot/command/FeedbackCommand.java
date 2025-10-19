package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.service.UserService;
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
public class FeedbackCommand implements IBotCommand {

    private final UserService userService;

    @Override
    public String getCommandIdentifier() {
        return "feedback";
    }

    @Override
    public String getDescription() {
        return "Отправить отзыв или предложение";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        if(strings.length == 0) {
            answer.setText("""
                    💌 Пожалуйста, напишите ваш отзыв или предложение после команды:
                    
                    /feedback [ваш текст]
                    
                    📝 Пример:
                    /feedback Отличный бот! Добавьте, пожалуйста, напоминания о приеме витаминов.""");
        } else {
            String feedbackText = String.join(" ", strings);
            userService.saveFeedback(message.getChatId(), feedbackText);
            answer.setText("✅ Спасибо за ваш отзыв! Мы обязательно его рассмотрим.");
        }
        sendMessage(answer, absSender);
    }
}
