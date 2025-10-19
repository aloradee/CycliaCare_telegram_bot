package com.telegrambot.cyclebot.bot.command;

import com.telegrambot.cyclebot.model.Feedback;
import com.telegrambot.cyclebot.service.FeedbackService;
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
public class FeedbackCommand implements IBotCommand {

    private final FeedbackService feedbackService;

    @Override
    public String getCommandIdentifier() {
        return "feedback";
    }

    @Override
    public String getDescription() {
        return "Отправить отзыв или предложение";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long chatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);

        if (arguments.length == 0) {
            answer.setText("""
                    💌 Форма обратной связи
                    
                    📝 Чтобы отправить отзыв, используйте:
                    /feedback [ваше сообщение]
                    
                    🎯 Вы также можете указать тип отзыва:
                    /feedback bug [описание ошибки] - сообщить об ошибке
                    /feedback feature [идея] - предложить функцию
                    /feedback question [вопрос] - задать вопрос
                    
                    ⭐ Оцените бот (от 1 до 5 звезд):
                    /feedback rating 5 [ваш отзыв]
                    
                    💡 Примеры:
                    /feedback Отличный бот! Спасибо!
                    /feedback bug Не работает команда /calendar
                    /feedback feature Добавьте напоминания о воде
                    /feedback rating 5 Очень удобный интерфейс!""");
        } else {
            String feedbackText = String.join(" ", arguments);
            Feedback.FeedbackType type = determineFeedbackType(arguments);
            Integer rating = extractRating(arguments);

            // Сохраняем отзыв
            feedbackService.saveFeedback(chatId, feedbackText, type, rating);

            String response = "✅ Спасибо за ваш отзыв! Мы обязательно его рассмотрим.";
            if (rating != null) {
                response += "\n⭐ Спасибо за оценку " + rating + "/5!";
            }
            if (type == Feedback.FeedbackType.BUG_REPORT) {
                response += "\n🐞 Мы уже работаем над исправлением ошибки.";
            } else if (type == Feedback.FeedbackType.FEATURE_REQUEST) {
                response += "\n💡 Идея добавлена в список планируемых функций!";
            }

            answer.setText(response);
        }

        sendMessage(answer, absSender);
    }

    /**
     * Определяет тип отзыва по первому аргументу
     */
    private Feedback.FeedbackType determineFeedbackType(String[] arguments) {
        if (arguments.length > 0) {
            String firstArg = arguments[0].toLowerCase();
            return switch (firstArg) {
                case "bug" -> Feedback.FeedbackType.BUG_REPORT;
                case "feature" -> Feedback.FeedbackType.FEATURE_REQUEST;
                case "question" -> Feedback.FeedbackType.QUESTION;
                case "complaint" -> Feedback.FeedbackType.COMPLAINT;
                case "rating" -> Feedback.FeedbackType.GENERAL_FEEDBACK;
                default -> Feedback.FeedbackType.GENERAL_FEEDBACK;
            };
        }
        return Feedback.FeedbackType.GENERAL_FEEDBACK;
    }

    /**
     * Извлекает рейтинг из аргументов
     */
    private Integer extractRating(String[] arguments) {
        if (arguments.length > 1 && "rating".equals(arguments[0].toLowerCase())) {
            try {
                int rating = Integer.parseInt(arguments[1]);
                if (rating >= 1 && rating <= 5) {
                    return rating;
                }
            } catch (NumberFormatException e) {
                // Рейтинг не указан или невалидный
            }
        }
        return null;
    }
}