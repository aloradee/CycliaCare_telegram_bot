package com.telegrambot.cyclebot.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    private Integer cycleLength = 28;

    private LocalDate lastPeriodStart;

    private LocalDate nextPeriodStart;

    private LocalDate ovulationDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Cycle> cycles = new ArrayList<>();
}
