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
public class HistoryCommand implements IBotCommand {

    private final CycleService cycleService;

    @Override
    public String getCommandIdentifier() {
        return "history";
    }

    @Override
    public String getDescription() {
        return "История циклов и симптомов";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        String history = cycleService.getHistory(message.getChatId());

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText(history);

        sendMessage(answer, absSender);
    }
}
