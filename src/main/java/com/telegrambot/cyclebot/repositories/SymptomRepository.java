package com.telegrambot.cyclebot.repositories;

import com.telegrambot.cyclebot.model.Symptom;
import com.telegrambot.cyclebot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    List<Symptom> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate start, LocalDate end);
    List<Symptom> findByUserOrderByDateDesc(User user);
}