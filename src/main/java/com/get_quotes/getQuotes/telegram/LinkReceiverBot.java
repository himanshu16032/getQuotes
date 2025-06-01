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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
    private SaveDataService saveDataService;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_SECONDS = 5;
    private static final String WEBHOOK_DELETE_URL = "https://api.telegram.org/bot8023873571:AAGdknd_uaDHzlMcvv9u1cdiWQ84ihsX9sg/deleteWebhook";

    Logger logger = LoggerFactory.getLogger(LinkReceiverBot.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        try {
            // First, ensure any existing webhook is deleted using direct API call
            deleteWebhookWithCurl();
            
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
     * Delete webhook using direct API call (equivalent to your curl command)
     */
    private void deleteWebhookWithCurl() {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                logger.info("Attempting to delete webhook using direct API call - Attempt {}", attempt);
                
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                    WEBHOOK_DELETE_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
                );
                
                logger.info("Webhook deletion attempt {}: Status = {}, Response = {}", 
                           attempt, response.getStatusCode(), response.getBody());
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("Successfully deleted webhook via API call on attempt {}", attempt);
                    return;
                }
                
            } catch (Exception e) {
                logger.warn("Attempt {} to delete webhook via API call failed: {}", attempt, e.getMessage());
                e.printStackTrace();
                
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
        
        logger.info("Failed to delete webhook via API call after {} attempts, trying fallback method", MAX_RETRY_ATTEMPTS);
        deleteWebhookWithFallback();
    }

    /**
     * Fallback webhook deletion using Telegram Bot API library
     */
    private void deleteWebhookWithFallback() {
        try {
            DeleteWebhook deleteWebhook = new DeleteWebhook();
            deleteWebhook.setDropPendingUpdates(true);
            Boolean result = execute(deleteWebhook);
            logger.info("Fallback webhook deletion result: {}", result);
        } catch (TelegramApiException e) {
            logger.warn("Fallback webhook deletion also failed: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manual webhook deletion method - can be called via REST endpoint if needed
     */
    public String manualDeleteWebhook() {
        try {
            logger.info("Manual webhook deletion requested");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                WEBHOOK_DELETE_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            String result = "Manual webhook deletion: Status = " + response.getStatusCode() + 
                           ", Response = " + response.getBody();
            logger.info(result);
            return result;
            
        } catch (Exception e) {
            String error = "Manual webhook deletion failed: " + e.getMessage();
            logger.error(error, e);
            return error;
        }
    }

    /**
     * Periodic webhook deletion to prevent conflicts (every 10 minutes)
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void periodicDeleteWebhook() {
        try {
            logger.debug("Running periodic webhook deletion check via API call");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                WEBHOOK_DELETE_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.debug("Periodic webhook deletion successful via API call");
            }
            
        } catch (Exception e) {
            // This is expected if no webhook exists, so just debug log
            logger.debug("Periodic webhook deletion via API call: {}", e.getMessage());
            
            // Try fallback method
            try {
                DeleteWebhook deleteWebhook = new DeleteWebhook();
                deleteWebhook.setDropPendingUpdates(false);
                Boolean result = execute(deleteWebhook);
                if (Boolean.TRUE.equals(result)) {
                    logger.debug("Periodic webhook deletion successful via fallback method");
                }
            } catch (TelegramApiException fallbackException) {
                logger.debug("Periodic webhook deletion fallback: {}", fallbackException.getMessage());
            }
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
