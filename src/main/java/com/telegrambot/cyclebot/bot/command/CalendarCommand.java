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
public class CalendarCommand implements IBotCommand {

    private final CycleService cycleService;

    @Override
    public String getCommandIdentifier() {
        return "calendar";
    }

    @Override
    public String getDescription() {
        return "Календарь предстоящих событий";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        String calendar = cycleService.getCalendar(message.getChatId());

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText(calendar);

        sendMessage(answer, absSender);
    }
}
