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

        // ПРАВИЛЬНЫЙ расчет следующих дат
        LocalDate nextPeriod = startDate.plusDays(user.getCycleLength());
        LocalDate ovulation = startDate.plusDays(user.getCycleLength() - 14);

        cycle.setPredictedNextStart(nextPeriod);
        cycleRepository.save(cycle);

        // Обновляем пользователя
        user.setLastPeriodStart(startDate);
        user.setNextPeriodStart(nextPeriod);
        user.setOvulationDate(ovulation);

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
                nextPeriod.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                ovulation.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                user.getCycleLength());
    }

    public String setCycleLength(Long chatId, Integer cycleLength) {
        try {
            if (cycleLength < 21 || cycleLength > 35) {
                return "❌ Длина цикла должна быть от 21 до 35 дней.";
            }

            User user = userService.getUser(chatId);
            Integer oldLength = user.getCycleLength();
            user.setCycleLength(cycleLength);

            // Пересчитываем прогнозы если цикл уже начат
            if (user.getLastPeriodStart() != null) {
                updateCyclePredictions(user);
                userService.registerUser(new org.telegram.telegrambots.meta.api.objects.User(
                        user.getChatId(), user.getFirstName(), false,
                        user.getLastName(), user.getUsername(), null, false, false, false, false, false
                ), user.getChatId());

                return String.format("""
                    ✅ Длина цикла изменена!
                    
                    📏 Было: %d дней
                    📏 Стало: %d дней
                    
                    🔄 Новые расчеты:
                    🩸 Следующая менструация: %s
                    🎯 Овуляция: %s
                    
                    💡 Овуляция рассчитывается как: длина цикла - 14 дней""",
                        oldLength, cycleLength,
                        user.getNextPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        user.getOvulationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                userService.registerUser(new org.telegram.telegrambots.meta.api.objects.User(
                        user.getChatId(), user.getFirstName(), false,
                        user.getLastName(), user.getUsername(), null, false, false, false, false, false
                ), user.getChatId());

                return String.format("""
                    ✅ Длина цикла установлена: %d дней
                    
                    💡 Когда вы начнете цикл (/startperiod), расчеты будут использовать эту длину.
                    🎯 Овуляция будет рассчитываться автоматически (цикл - 14 дней)""",
                        cycleLength);
            }
        } catch (Exception e) {
            log.error("Error setting cycle length for user {}", chatId, e);
            return "❌ Ошибка при установке длины цикла.";
        }
    }

    public String markPeriodEnd(Long chatId, LocalDate endDate) {
        try {
            User user = userService.getUser(chatId);

            if (user.getLastPeriodStart() == null) {
                return """
                   ❌ Сначала нужно начать цикл!
                   
                   💡 Используйте /startperiod чтобы отметить начало месячных.""";
            }

            if (endDate.isBefore(user.getLastPeriodStart())) {
                return "❌ Дата окончания не может быть раньше даты начала месячных.";
            }

            user.setLastPeriodEnd(endDate);

            // Рассчитываем длительность месячных
            long periodLength = ChronoUnit.DAYS.between(user.getLastPeriodStart(), endDate) + 1;
            user.setPeriodLength((int) periodLength);

            userService.registerUser(new org.telegram.telegrambots.meta.api.objects.User(
                    user.getChatId(), user.getFirstName(), false,
                    user.getLastName(), user.getUsername(), null, false, false, false, false, false
            ), user.getChatId());

            return String.format("""
                ✅ Конец месячных отмечен!
                
                📅 Начало: %s
                📅 Окончание: %s
                📏 Длительность: %d дней
                
                💡 Используйте /status для просмотра полной информации""",
                    user.getLastPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    periodLength);

        } catch (Exception e) {
            log.error("Error marking period end for user {}", chatId, e);
            return "❌ Ошибка при отметке конца месячных.";
        }
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

            StringBuilder status = new StringBuilder();
            status.append(String.format("""
                📊 Текущий статус:
                
                🌙 Фаза: %s
                📅 Сегодня: %s
                
                📍 Последние месячные:
                🩸 Начало: %s""",
                    phase,
                    today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getLastPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

            if (user.getLastPeriodEnd() != null) {
                status.append(String.format("\n🩸 Окончание: %s",
                        user.getLastPeriodEnd().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
                status.append(String.format("\n📏 Длительность: %d дней", user.getPeriodLength()));
            } else {
                status.append("\n💡 Используйте /endperiod чтобы отметить конец месячных");
            }

            status.append(String.format("""
                
                🔮 Прогноз:
                🩸 Следующая менструация: %s (%d дней)
                🎯 Овуляция: %s
                📏 Длина цикла: %d дней
                
                ⚙️ Настройки:
                🎯 Смещение овуляции: %d дней (цикл - %d)""",
                    user.getNextPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    daysUntilNext,
                    user.getOvulationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getCycleLength(),
                    user.getOvulationOffset(),
                    user.getOvulationOffset()));

            return status.toString();

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
            LocalDate ovulation = nextPeriod.minusDays(user.getOvulationOffset());

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
    public String changeCycleStartDate(Long chatId, LocalDate newStartDate) {
        try {
            User user = userService.getUser(chatId);

            if (user.getLastPeriodStart() == null) {
                return """
                   ❌ У вас еще нет начатого цикла!
                   
                   💡 Сначала используйте /startperiod чтобы начать отслеживание.""";
            }

            LocalDate oldStartDate = user.getLastPeriodStart();

            // Находим последний цикл и обновляем его
            Cycle lastCycle = cycleRepository.findTopByUserOrderByStartDateDesc(user)
                    .orElseThrow(() -> new RuntimeException("Cycle not found"));

            // Обновляем цикл
            lastCycle.setStartDate(newStartDate);
            lastCycle.setPredictedNextStart(newStartDate.plusDays(user.getCycleLength()));
            cycleRepository.save(lastCycle);

            // Обновляем пользователя
            user.setLastPeriodStart(newStartDate);
            updateCyclePredictions(user);
            userService.registerUser(new org.telegram.telegrambots.meta.api.objects.User(
                    user.getChatId(), user.getFirstName(), false,
                    user.getLastName(), user.getUsername(), null, false, false, false, false, false
            ), user.getChatId());

            return String.format("""
                ✅ Дата начала цикла изменена!
                
                📅 Было: %s
                📅 Стало: %s
                
                🔄 Новые расчеты:
                🩸 Следующая менструация: %s
                🎯 Овуляция: %s
                
                💡 Используйте /status для проверки нового статуса""",
                    oldStartDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    newStartDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getNextPeriodStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    user.getOvulationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        } catch (Exception e) {
            log.error("Error changing cycle start date for user {}", chatId, e);
            return "❌ Ошибка при изменении даты. Убедитесь, что цикл был начат ранее.";
        }
    }
}