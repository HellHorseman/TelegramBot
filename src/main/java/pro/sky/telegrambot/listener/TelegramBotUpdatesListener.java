package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.:\\s]{16})(\\s)(.+)");

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::updateReceived);
        logger.info("Processing update: {}", updates);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void updateReceived(Update update) {
        if (update.message() == null) {
            return;
        }

        String messageText = update.message().text();
        long chatId = update.message().chat().id();

        switch (messageText) {
            case "/start":
                sendMessage(chatId);
                break;
            default:
                logger.info("Unexpected message");
        }

        Matcher matcher = MESSAGE_PATTERN.matcher(messageText);
        if (matcher.find()) {
            matchedMessage(chatId, matcher);
        } else {
            sendFormatError(chatId);
        }
    }

    private void sendMessage(long chatId) {
        logger.info("Sending message to user");
        telegramBot.execute(new SendMessage(chatId, "Вас приветствует бот-напоминалка "
                + "Готов вкалывать!"));
    }
}


