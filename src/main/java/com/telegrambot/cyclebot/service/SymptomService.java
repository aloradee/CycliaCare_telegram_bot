package com.telegrambot.cyclebot.service;

import com.telegrambot.cyclebot.model.Symptom;
import com.telegrambot.cyclebot.model.User;
import com.telegrambot.cyclebot.repositories.SymptomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SymptomService {

    private final UserService userService;
    private final SymptomRepository symptomRepository;

    public String addSymptom(Long chatId, String symptomType, String severity) {
        User user = userService.getUser(chatId);

        Symptom symptom = new Symptom();
        symptom.setUser(user);
        symptom.setSymptomType(symptomType);
        symptom.setSeverity(severity);

        symptomRepository.save(symptom);

        return String.format("""
                ✅ Симптом записан!
                
                🎯 Тип: %s
                📊 Интенсивность: %s
                📅 Дата: %s
                
                💡 Используйте /history для просмотра всех симптомов""",
                symptomType, severity,
                symptom.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
}