package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.ScheduledTask;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    //Create pattern which will find regular conversation//
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.:\\s]{16})(\\s)(.+)");
    //Initialize pattern format
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private TelegramBot telegramBot;

    private final ScheduledTask scheduledTask;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, ScheduledTask sheduledTask) {
        this.telegramBot = telegramBot;
        this.scheduledTask = sheduledTask;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    //Null checking method
    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::updateReceived);
        logger.info("Processing update: {}", updates);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    //Method to update incoming message
    public void updateReceived(Update update) {
        if (update.message() == null) {
            return;
        }

        String messageText = update.message().text();
        long chatId = update.message().chat().id();

        if ("/start".equals(messageText)) {
            sendMessage(chatId);
            logger.info("Sending message to user");
            return;
        }

        Matcher matcher = MESSAGE_PATTERN.matcher(messageText);
        if (matcher.find()) {
            matchedMessage(chatId, matcher);
        } else {
            sendFormatError(chatId);
        }
    }

    //Send message method
    private void sendMessage(long chatId) {
        logger.info("Sending message to user");
        telegramBot.execute(new SendMessage(chatId, "Вас приветствует бот-напоминалка "
                + "Готов вкалывать!"));
    }

    //Find and check pattern method
    private void matchedMessage(long chatId, Matcher matcher) {
        String dataTimeStr = matcher.group(1);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dataTimeStr, DATE_TIME_FORMATTER);

            if (dateTime.isBefore(LocalDateTime.now())) {
                sendPastTimeError(chatId);
                return;
            }

            String notificationText = matcher.group(3);
            scheduledTask.saveNotification(chatId, notificationText, dateTime);
            telegramBot.execute(new SendMessage(chatId, "Дело сделано!"));
            logger.info("Send confirm message");

        } catch (DateTimeParseException e) {
            sendFormatError(chatId);
        }

    }

    //Send time error
    private void sendPastTimeError(long chatId) {
        telegramBot.execute(new SendMessage(chatId, "Что было, то было. Назад не воротишь!"));
        logger.info("Send past time error");
    }

    //Send format error
    private void sendFormatError(long chatId) {
        telegramBot.execute(new SendMessage(chatId, "Введите сообщение в формате: dd.MM.yyyy HH:mm Ваш текст"));
        logger.info("Send format error");
    }
}


