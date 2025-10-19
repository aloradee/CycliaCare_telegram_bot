package com.telegrambot.cyclebot.controller;

import com.telegrambot.cyclebot.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public String feedbackList(Model model) {
        model.addAttribute("feedbacks", feedbackService.getUnprocessedFeedback());
        model.addAttribute("unprocessedCount", feedbackService.getUnprocessedCount());
        return "admin/feedback-list";
    }

    @PostMapping("/{id}/process")
    public String processFeedback(@PathVariable Long id) {
        feedbackService.markAsProcessed(id);
        return "redirect:/admin/feedback";
    }
}