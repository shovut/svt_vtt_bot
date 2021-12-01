package su.shovuty.svt_vtt_bot.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasVoice()) {
            sendMsg(message.getChatId(), message.getMessageId());
        }

        if (message != null && message.getNewChatMembers() != null) {
            for (User chatMember : message.getNewChatMembers()) {
                if (chatMember.getIsBot() && chatMember.getUserName().equals(getBotUsername())){
                    sendInitMsg(message.getChatId());
                    break;
                }
            }
        }

    }

    private void sendMsg(long chatId, int messageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Осуждаю голосовые сообщения");
        sendMessage.setReplyToMessageId(messageId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception: {}", e.toString());
        }
    }

    private void sendInitMsg(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Я бот, который будет переводить ваши голосовые сообщения в нормальный вид.");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Exception: {}", e.toString());
        }
    }
}
