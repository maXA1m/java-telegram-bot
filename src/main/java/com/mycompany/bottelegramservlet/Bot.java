package com.mycompany.bottelegramservlet;

import java.util.logging.Level;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.LongPollingBot;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class Bot extends TelegramLongPollingBot {
    final String[] COMMON_PHRASES = {
        "Нет ничего ценнее слов, сказанных к месту и ко времени.",
        "Порой молчание может сказать больше, нежели уйма слов.",
        "Перед тем как писать/говорить всегда лучше подумать.",
        "Вежливая и грамотная речь говорит о величии души.",
        "Приятно когда текст без орфографических ошибок.",
        "Многословие есть признак неупорядоченного ума.",
        "Слова могут ранить, но могут и исцелять.",
        "Записывая слова, мы удваиваем их силу.",
        "Кто ясно мыслит, тот ясно излагает.",
        "Боюсь Вы что-то не договариваете."};
    final String[] ELUSIVE_ANSWERS = {
        "Вопрос непростой, прошу тайм-аут на раздумья.",
        "Не уверен, что располагаю такой информацией.",
        "Может лучше поговорим о чём-то другом?",
        "Простите, но это очень личный вопрос.",
        "Не уверен, что Вам понравится ответ.",
        "Поверьте, я сам хотел бы это знать.",
        "Вы действительно хотите это знать?",
        "Уверен, Вы уже догадались сами.",
        "Зачем Вам такая информация?",
        "Давайте сохраним интригу?"};
    final Map<String, String> PATTERNS_FOR_ANALYSIS = new HashMap<String, String>() {{
        // hello
        put("хай", "hello");
        put("привет", "hello");
        put("здорово", "hello");
        put("здравствуй", "hello");
        // who
        put("кто\\s.*ты", "who");
        put("ты\\s.*кто", "who");
        // name
        put("как\\s.*зовут", "name");
        put("как\\s.*имя", "name");
        put("есть\\s.*имя", "name");
        put("какое\\s.*имя", "name");
        // howareyou
        put("как\\s.*дела", "howareyou");
        put("как\\s.*жизнь", "howareyou");
        // whatdoyoudoing
        put("зачем\\s.*тут", "whatdoyoudoing");
        put("зачем\\s.*здесь", "whatdoyoudoing");
        put("что\\s.*делаешь", "whatdoyoudoing");
        put("чем\\s.*занимаешься", "whatdoyoudoing");
        // whatdoyoulike
        put("что\\s.*нравится", "whatdoyoulike");
        put("что\\s.*любишь", "whatdoyoulike");
        // iamfeelling
        put("кажется", "iamfeelling");
        put("чувствую", "iamfeelling");
        put("испытываю", "iamfeelling");
        // yes
        put("^да", "yes");
        put("согласен", "yes");
        // whattime
        put("который\\s.*час", "whattime");
        put("сколько\\s.*время", "whattime");
        // bye
        put("прощай", "bye");
        put("увидимся", "bye");
        put("до\\s.*свидания", "bye");
    }};
    final Map<String, String> ANSWERS_BY_PATTERNS = new HashMap<String, String>() {{
        put("hello", "Здравствуйте, рад Вас видеть.");
        put("who", "Я обычный чат-бот.");
        put("name", "Зовите меня Чаттер :)");
        put("howareyou", "Спасибо, что интересуетесь. У меня всё хорошо.");
        put("whatdoyoudoing", "Я пробую общаться с людьми.");
        put("whatdoyoulike", "Мне нравиться думать что я не просто программа.");
        put("iamfeelling", "Как давно это началось? Расскажите чуть подробнее.");
        put("yes", "Согласие есть продукт при полном непротивлении сторон.");
        put("bye", "До свидания. Надеюсь, ещё увидимся.");
    }};
    Pattern pattern; // for regexp
    Random random; // for random answers
    Date date; // for date and time

    public Bot() {
        random = new Random();
        date = new Date();
    }

    static LongPollingBot getBot() {
        return new Bot();
    }
    
    @Override
    public String getBotUsername() {
        return "@AIJavaBot";
    }
    
    @Override
    public String getBotToken() {
        return "532575158:AAGqkM70GhSriXikJksaWIR7U6rWj88h51Q";
    }
    
    @Override
      public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String messageReturn = "";
            String message = String.join(" ", messageText.toLowerCase().split("[ {,|.}?]+"));
            
            if(messageText.equals("/start") || messageText.equals("/help")){
                messageReturn = "Привет, я умный бот, давай пообщаемся :)";
            }
            else{
                for (Map.Entry<String, String> o : PATTERNS_FOR_ANALYSIS.entrySet()) {
                    pattern = Pattern.compile(o.getKey());
                    if (pattern.matcher(message).find())
                        if (o.getValue().equals("whattime"))
                            messageReturn = date.toString();

                        else
                            messageReturn = ANSWERS_BY_PATTERNS.get(o.getValue());
                    else
                        messageReturn = (messageText.trim().endsWith("?"))? ELUSIVE_ANSWERS[random.nextInt(ELUSIVE_ANSWERS.length)]: COMMON_PHRASES[random.nextInt(COMMON_PHRASES.length)];
                }
            }
            
            SendMessage sendMessage = new SendMessage().setChatId(chat_id).setText(messageReturn);         
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException ex) {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
      }
}
