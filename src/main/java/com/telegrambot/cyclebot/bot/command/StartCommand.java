package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.model.User;
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
public class StartCommand implements IBotCommand {

    private final UserService userService;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        // Регистрируем пользователя и получаем сохраненного пользователя
        User user = userService.registerUser(message.getFrom(), message.getChatId());

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText("""
            🌸 Добро пожаловать в CycleCare!
            
            Я помогу вам отслеживать менструальный цикл, симптомы и настроение.
            
            📋 Основные команды:
            /startperiod - Начать отсчет цикла
            /changeperiod - Изменить дату начала цикла
            /setcycle - Установить длину цикла
            /endperiod - Отметить конец месячных
            /symptom - Добавить симптомы
            /status - Текущий статус цикла
            /calendar - Календарь событий
            /history - История циклов
            /feedback - Обратная связь
            /help - Помощь
            
            💡 Для начала работы введите /startperiod""");

        sendMessage(answer, absSender);
    }
}