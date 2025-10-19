package com.telegrambot.cyclebot.repositories;

import com.telegrambot.cyclebot.model.Cycle;
import com.telegrambot.cyclebot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CycleRepository extends JpaRepository<Cycle, Long> {
    List<Cycle> findByUserOrderByStartDateDesc(User user);
    Optional<Cycle> findTopByUserOrderByStartDateDesc(User user);
}