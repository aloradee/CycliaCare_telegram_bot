package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.service.SymptomService;
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
public class SymptomCommand implements IBotCommand {

    private final SymptomService symptomService;

    @Override
    public String getCommandIdentifier() {
        return "symptom";
    }

    @Override
    public String getDescription() {
        return "Добавить симптомы (используйте: /symptom [тип] [интенсивность])";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        if(strings.length < 2) {
            answer.setText("""
                    📝 Для добавления симптома используйте:
                    /symptom [тип] [интенсивность]
                    
                    🎯 Типы симптомов:
                    - головная_боль
                    - боль_в_животе  
                    - тошнота
                    - усталость
                    - настроение
                    
                    📊 Интенсивность:
                    - низкая
                    - средняя
                    - высокая
                    
                    💡 Пример: /symptom головная_боль средняя""");
        } else {
            String symptomType = strings[0];
            String severity = strings[1];
            String result = symptomService
        }
        sendMessage(answer, absSender);
    }
}
