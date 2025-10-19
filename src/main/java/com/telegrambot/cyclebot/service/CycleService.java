package com.telegrambot.cyclebot.service;

import com.telegrambot.cyclebot.model.Cycle;
import com.telegrambot.cyclebot.model.User;
import com.telegrambot.cyclebot.repositories.CycleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CycleService {

    private final UserService userService;
    private final CycleRepository cycleRepository;

    public String startNewCycle(Long chatId, LocalDate startDate) {
        User user = userService.getUser(chatId);

        // Сохраняем цикл
        Cycle cycle = new Cycle();
        cycle.setUser(user);
        cycle.setStartDate(startDate);
        cycle.setCycleLength(user.getCycleLength());
        cycle.setPredictedNextStart(startDate.plusDays(user.getCycleLength()));
        cycleRepository.save(cycle);

        // Обновляем пользователя
        user.setLastPeriodStart(startDate);
        updateCyclePredictions(user);
        userService.registerUser(new org.telegram.telegrambots.meta.api.objects.User(
                user.getChatId(), user.getFirstName(), false,
                user.getLastName(), user.getUsername(), null, false, false, false, false, false
        ), user.getChatId());

        return String.format("""
                ✅ Цикл начат %s
                
                📅 Следующая менструация: %s
                🎯 Овуляция: %s
                📏 Длина цикла: %d дней
                
                💡 Используйте /status для текущего статуса""",
                startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                user.getNextPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                user.getOvulationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                user.getCycleLength());
    }

    public String getCurrentStatus(Long chatId) {
        try {
            User user = userService.getUser(chatId);

            if (user.getLastPeriodStart() == null) {
                return """
                       ❓ Данные о цикле не введены.
                       💡 Используйте /startperiod чтобы начать отслеживание.""";
            }

            LocalDate today = LocalDate.now();
            String phase = getCurrentPhase(user, today);
            long daysUntilNext = ChronoUnit.DAYS.between(today, user.getNextPeriodStart());

            return String.format("""
                    📊 Текущий статус:
                    
                    🌙 Фаза: %s
                    📅 Сегодня: %s
                    📍 Следующая менструация: %s (%d дней)
                    🎯 Овуляция: %s
                    
                    💪 Длина цикла: %d дней""",
                    phase,
                    today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getNextPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    daysUntilNext,
                    user.getOvulationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getCycleLength());

        } catch (Exception e) {
            return """
                   ❓ Данные о цикле не введены.
                   💡 Используйте /startperiod чтобы начать отслеживание.""";
        }
    }

    public String getCalendar(Long chatId) {
        try {
            User user = userService.getUser(chatId);

            if (user.getLastPeriodStart() == null) {
                return "❓ Сначала начните отслеживание цикла с помощью /startperiod";
            }

            return String.format("""
                    📅 Календарь событий:
                    
                    🩸 Последняя менструация: %s
                    🩸 Следующая менструация: %s
                    🎯 Овуляция: %s
                    📏 Длина цикла: %d дней
                    
                    💡 Используйте /status для детального статуса""",
                    user.getLastPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getNextPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getOvulationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getCycleLength());

        } catch (Exception e) {
            return "❓ Сначала начните отслеживание цикла с помощью /startperiod";
        }
    }

    public String getHistory(Long chatId) {
        try {
            User user = userService.getUser(chatId);
            List<Cycle> cycles = cycleRepository.findByUserOrderByStartDateDesc(user);

            if (cycles.isEmpty()) {
                return "📝 История циклов пуста.\n💡 Используйте /startperiod чтобы добавить первый цикл.";
            }

            StringBuilder history = new StringBuilder("📝 История циклов:\n\n");
            for (int i = 0; i < Math.min(cycles.size(), 5); i++) {
                Cycle cycle = cycles.get(i);
                history.append(String.format("🔹 %s - %d дней\n",
                        cycle.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        cycle.getCycleLength()));
            }

            if (cycles.size() > 5) {
                history.append(String.format("\n... и еще %d циклов", cycles.size() - 5));
            }

            return history.toString();

        } catch (Exception e) {
            return "❓ Сначала начните отслеживание цикла с помощью /startperiod";
        }
    }

    private void updateCyclePredictions(User user) {
        if (user.getLastPeriodStart() != null) {
            LocalDate nextPeriod = user.getLastPeriodStart().plusDays(user.getCycleLength());
            LocalDate ovulation = nextPeriod.minusDays(14);

            user.setNextPeriodStart(nextPeriod);
            user.setOvulationDate(ovulation);
        }
    }

    private String getCurrentPhase(User user, LocalDate today) {
        if (today.isBefore(user.getOvulationDate())) {
            return "Фолликулярная фаза";
        } else if (today.isEqual(user.getOvulationDate())) {
            return "Овуляция";
        } else if (today.isBefore(user.getNextPeriodStart())) {
            return "Лютеиновая фаза";
        } else {
            return "Менструация";
        }
    }
}