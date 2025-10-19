package com.telegrambot.cyclebot.service;

import com.telegrambot.cyclebot.model.User;
import com.telegrambot.cyclebot.repositories.SymptomRepository;
import com.telegrambot.cyclebot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CycleService cycleService;
    @Autowired
    private SymptomRepository symptomRepository;

    public void handleStartCommand(Long chatId, String firstName) {
        User user = userRepository.findById(chatId).orElse(new User());
        user.setChatId(chatId);
        user.setFirstName(firstName);
        userRepository.save(user);

        String welcome = "Добро пожаловать, " + firstName + "! Я помогу отслеживать твой цикл.\n\n" +
                "Основные команды:\n" +
                "/startperiod - Начать отсчет цикла\n" +
                "/symptom - Добавить симптомы\n" +
                "/status - Текущий статус\n" +
                "/calendar - Календарь событий\n" +
                "/feedback - Обратная связь";


    }

}
