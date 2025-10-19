package com.telegrambot.cyclebot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long chatId;

    private String firstName;
    private String lastName;
    private String username;

    private Integer cycleLength = 28; // Длина цикла по умолчанию
    private Integer periodLength = 5;  // Длина месячных по умолчанию
    private Integer ovulationOffset = 14; // Смещение овуляции

    private LocalDate lastPeriodStart;
    private LocalDate lastPeriodEnd;   // Конец последних месячных
    private LocalDate nextPeriodStart;
    private LocalDate ovulationDate;

    private LocalDate registeredAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cycle> cycles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Symptom> symptoms = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDate.now();
    }
}