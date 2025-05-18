package com.get_quotes.getQuotes.telegram;

import com.get_quotes.getQuotes.Service.Service.SaveDataService;
import com.get_quotes.getQuotes.Service.Service.SaveUserService;
import com.get_quotes.getQuotes.Service.pojo.SaveData;
import com.get_quotes.getQuotes.Utility.ThreadUtlity.ThreadLocalContext;
import com.get_quotes.getQuotes.Utility.ThreadUtlity.ThreadLocalContextKeys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LinkReceiverBot extends TelegramLongPollingBot {

    // Store user chat IDs who subscribed
    @Autowired
    private SaveUserService saveUserService;

    @Autowired
    private  SaveDataService saveDataService;

    Logger logger = LoggerFactory.getLogger(LinkReceiverBot.class);

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("Bot registered successfully!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register bot", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "GirlfriendManaoBot"; // e.g., my_link_notifier_bot
    }

    @Override
    public String getBotToken() {
        return "8023873571:AAGdknd_uaDHzlMcvv9u1cdiWQ84ihsX9sg"; // Get from @BotFather
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {

            if (update.hasMessage() && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                String messageText = update.getMessage().getText();
                saveUserService.action(String.valueOf(chatId));
                logger.info("received message: " + messageText + " from chatId: " + chatId);
                messageText = extractUrl(messageText);
                logger.info("Extracted message: " + messageText);
                if (messageText.equalsIgnoreCase("/start")) {
                    sendText(chatId, "Thanks for subscribing! Send me a link to get started.");
                } else if (messageText.startsWith("http")) {
                    ThreadLocalContext.set(ThreadLocalContextKeys.USER_ID, String.valueOf(chatId));
                    SaveData saveData = new SaveData();
                    saveData.setRequestLink(messageText);
                    saveDataService.action(saveData);
                    sendText(chatId, "Received your link successfully once price is droped we will send you a notification");
                    // You can trigger scraping here and reply later too
                } else {
                    sendText(chatId, "Send a valid link or use /start to subscribe. currently we support only myntra.com");
                }
            }
        }
        finally {
            ThreadLocalContext.clear();
        }
    }

    public void sendText(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static String extractUrl(String message) {
        String urlRegex = "(http[s]?://\\S+)";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find() && validateUrl(matcher.group(1))) {
            return matcher.group(1); // Return the extracted URL
        }
        return message; // Return null if no URL is found
    }


    public static  boolean validateUrl(String url) {
        if(url.contains("www.myntra.com"))return true;
        return false;
    }
}
