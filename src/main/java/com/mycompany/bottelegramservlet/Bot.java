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
        "��� ������ ������ ����, ��������� � ����� � �� �������.",
        "����� �������� ����� ������� ������, ������ ���� ����.",
        "����� ��� ��� ������/�������� ������ ����� ��������.",
        "�������� � ��������� ���� ������� � ������� ����.",
        "������� ����� ����� ��� ��������������� ������.",
        "����������� ���� ������� ���������������� ���.",
        "����� ����� ������, �� ����� � ��������.",
        "��������� �����, �� ��������� �� ����.",
        "��� ���� ������, ��� ���� ��������.",
        "����� �� ���-�� �� �������������."};
    final String[] ELUSIVE_ANSWERS = {
        "������ ���������, ����� ����-��� �� ��������.",
        "�� ������, ��� ���������� ����� �����������.",
        "����� ����� ��������� � ���-�� ������?",
        "��������, �� ��� ����� ������ ������.",
        "�� ������, ��� ��� ���������� �����.",
        "��������, � ��� ����� �� ��� �����.",
        "�� ������������� ������ ��� �����?",
        "������, �� ��� ���������� ����.",
        "����� ��� ����� ����������?",
        "������� �������� �������?"};
    final Map<String, String> PATTERNS_FOR_ANALYSIS = new HashMap<String, String>() {{
        // hello
        put("���", "hello");
        put("������", "hello");
        put("�������", "hello");
        put("����������", "hello");
        // who
        put("���\\s.*��", "who");
        put("��\\s.*���", "who");
        // name
        put("���\\s.*�����", "name");
        put("���\\s.*���", "name");
        put("����\\s.*���", "name");
        put("�����\\s.*���", "name");
        // howareyou
        put("���\\s.*����", "howareyou");
        put("���\\s.*�����", "howareyou");
        // whatdoyoudoing
        put("�����\\s.*���", "whatdoyoudoing");
        put("�����\\s.*�����", "whatdoyoudoing");
        put("���\\s.*�������", "whatdoyoudoing");
        put("���\\s.*�����������", "whatdoyoudoing");
        // whatdoyoulike
        put("���\\s.*��������", "whatdoyoulike");
        put("���\\s.*������", "whatdoyoulike");
        // iamfeelling
        put("�������", "iamfeelling");
        put("��������", "iamfeelling");
        put("���������", "iamfeelling");
        // yes
        put("^��", "yes");
        put("��������", "yes");
        // whattime
        put("�������\\s.*���", "whattime");
        put("�������\\s.*�����", "whattime");
        // bye
        put("������", "bye");
        put("��������", "bye");
        put("��\\s.*��������", "bye");
    }};
    final Map<String, String> ANSWERS_BY_PATTERNS = new HashMap<String, String>() {{
        put("hello", "������������, ��� ��� ������.");
        put("who", "� ������� ���-���.");
        put("name", "������ ���� ������ :)");
        put("howareyou", "�������, ��� �������������. � ���� �� ������.");
        put("whatdoyoudoing", "� ������ �������� � ������.");
        put("whatdoyoulike", "��� ��������� ������ ��� � �� ������ ���������.");
        put("iamfeelling", "��� ����� ��� ��������? ���������� ���� ���������.");
        put("yes", "�������� ���� ������� ��� ������ ������������� ������.");
        put("bye", "�� ��������. �������, ��� ��������.");
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
                messageReturn = "������, � ����� ���, ����� ���������� :)";
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
