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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LinkReceiverBot extends TelegramLongPollingBot {

    // Store user chat IDs who subscribed
    @Autowired
    private SaveUserService saveUserService;

    @Autowired
    private  SaveDataService saveDataService;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_SECONDS = 5;

    Logger logger = LoggerFactory.getLogger(LinkReceiverBot.class);

    @PostConstruct
    public void init() {
        try {
            // First, ensure any existing webhook is deleted
            deleteWebhookWithRetry();
            
            // Add a small delay to ensure webhook deletion is processed
            Thread.sleep(2000);
            
            // Register the bot with long polling
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            logger.info("Bot '{}' registered successfully with long polling!", getBotUsername());
            
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("Failed to register bot: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register bot", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("Interrupted while initializing bot", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Bot initialization interrupted", e);
        }
    }

    /**
     * Delete webhook with retry mechanism
     */
    private void deleteWebhookWithRetry() {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                DeleteWebhook deleteWebhook = new DeleteWebhook();
                deleteWebhook.setDropPendingUpdates(true); // Clear any pending updates
                
                Boolean result = execute(deleteWebhook);
                logger.info("Webhook deletion attempt {}: {}", attempt, result ? "Success" : "Failed");
                
                if (Boolean.TRUE.equals(result)) {
                    logger.info("Successfully deleted webhook on attempt {}", attempt);
                    return;
                }
                
            } catch (TelegramApiException e) {
                e.printStackTrace();
                logger.info("Attempt {} to delete webhook failed: {}", attempt, e.getMessage());
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during webhook deletion retry", ie);
                    }
                }
            }
        }
        
        logger.info("Failed to delete webhook after {} attempts, proceeding anyway", MAX_RETRY_ATTEMPTS);
    }

    /**
     * Periodic webhook deletion to prevent conflicts (every 10 minutes)
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void periodicDeleteWebhook() {
        try {
            logger.debug("Running periodic webhook deletion check");
            DeleteWebhook deleteWebhook = new DeleteWebhook();
            deleteWebhook.setDropPendingUpdates(false); // Don't drop updates during periodic check
            
            Boolean result = execute(deleteWebhook);
            if (Boolean.TRUE.equals(result)) {
                logger.debug("Periodic webhook deletion successful");
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            // This is expected if no webhook exists, so just debug log
            logger.debug("Periodic webhook deletion: {}", e.getMessage());
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error processing update: {}", e.getMessage(), e);
        } finally {
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
