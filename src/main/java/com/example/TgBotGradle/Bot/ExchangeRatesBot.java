package com.example.TgBotGradle.Bot;

import com.example.TgBotGradle.Exception.ServiceException;
import com.example.TgBotGradle.Service.ExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.rmi.server.ServerCloneException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String AED = "/aed";
    private static final String HELP = "/help";
    @Autowired
    private ExchangeRatesService exchangeRatesService;

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
        registerCommands();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            var callbackQueryId = update.getCallbackQuery().getId();
            var userMessage = update.getCallbackQuery().getFrom().getUserName();

            switch (data) {
                case "/usd":
                    usdCommand(chatId);
                    AnswerCallbackQuery answerUsd = new AnswerCallbackQuery();
                    answerUsd.setCallbackQueryId(callbackQueryId);
                    try {
                        execute(answerUsd);
                    } catch (TelegramApiException e) {
                        LOG.error("Ошибка отправки ответа на инлайн-кнопку", e);
                    }
                    LOG.info("press /usd: " + userMessage);
                    break;
                case "/eur":
                    eurCommand(chatId);
                    AnswerCallbackQuery answerEur = new AnswerCallbackQuery();
                    answerEur.setCallbackQueryId(callbackQueryId);
                    try {
                        execute(answerEur);
                    } catch (TelegramApiException e) {
                        LOG.error("Ошибка отправки ответа на инлайн-кнопку", e);
                    }
                    LOG.info("press /eur: " + userMessage);
                    break;
                case "/aed":
                    aedCommand(chatId);
                    AnswerCallbackQuery answerAed = new AnswerCallbackQuery();
                    answerAed.setCallbackQueryId(callbackQueryId);
                    try {
                        execute(answerAed);
                    } catch (TelegramApiException e) {
                        LOG.error("Ошибка отправки ответа на инлайн-кнопку", e);
                    }
                    LOG.info("press /aed: " + userMessage);
                    break;
                case "/help":
                    helpCommand(chatId);
                    AnswerCallbackQuery answerHelp = new AnswerCallbackQuery();
                    answerHelp.setCallbackQueryId(callbackQueryId);
                    try {
                        execute(answerHelp);
                    } catch (TelegramApiException e) {
                        LOG.error("Ошибка отправки ответа на инлайн-кнопку", e);
                    }
                    LOG.info("press /help: " + userMessage);
                    break;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();

            if (update.getMessage().getChat().getUserName() != null) {
                var userMessage = update.getMessage().getChat().getUserName();
                switch (message) {
                    case START -> {
                        startCommand(chatId, userMessage);
                        var currencySelectionMessage = createCurrencySelectionMessage(chatId);
                        try {
                            execute(currencySelectionMessage);
                        } catch (TelegramApiException e) {
                            LOG.error("Ошибка отправки сообщения", e);
                        }
                        LOG.info("press /start: " + userMessage);
                    }
                    case USD -> {
                        usdCommand(chatId);
                        LOG.info("press /usd: " + userMessage);
                    }
                    case EUR -> {
                        eurCommand(chatId);
                        LOG.info("press /eur: " + userMessage);
                    }
                    case AED -> {
                        aedCommand(chatId);
                        LOG.info("press /aed: " + userMessage);
                    }
                    case HELP -> {
                        helpCommand(chatId);
                        LOG.info("press /help: " + userMessage);
                    }
                    default -> {
                        unknownCommand(chatId);
                        LOG.info("press unknown command: " + userMessage);
                    }
                }
            }
        }
    }


    @Override
    public String getBotUsername() {
        return "currenciesjavaspringbot";
    }

    public void registerCommands() {
        List<BotCommand> commands = new ArrayList<>();

        BotCommand startCommand = new BotCommand("start", "Начать работу с ботом");
        BotCommand usdCommand = new BotCommand("usd", "Получить курс доллара");
        BotCommand eurCommand = new BotCommand("eur", "Получить курс евро");
        BotCommand aedCommand = new BotCommand("aed", "Получить курс дирхама");
        BotCommand helpCommand = new BotCommand("help", "Получить справку");

        commands.add(startCommand);
        commands.add(usdCommand);
        commands.add(eurCommand);
        commands.add(aedCommand);
        commands.add(helpCommand);

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);

        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка регистрации команд", e);
        }
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("ошибка отправки сообщения ", e);
        }
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                                
                Здесь Вы сможете узнать официальные курсы валют на сегодня.
                             
                Для этого воспользуйтесь копками синзу
                либо напишите команду:
                /usd - курс доллара
                /eur - курс евро
                /aed - курс дирхама
                                
                Дополнительные команда
                либо кнопка снизу:
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void usdCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getUSDExchangeRate();
            var text = "Курс доллара на %s составляет %s грн";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса доллара", e);
            formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже.";
        } catch (ServerCloneException e) {
            throw new RuntimeException(e);
        }
        sendMessage(chatId, formattedText);
    }

    private void eurCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getEURExchangeRate();
            var text = "Курс евро на %s составляет %s грн";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса евро", e);
            formattedText = "Не удалось получить текущий курс евро. Попробуйте позже.";
        } catch (ServerCloneException e) {
            throw new RuntimeException(e);
        }
        sendMessage(chatId, formattedText);
    }

    private void aedCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getAEDExchangeRate();
            var text = "Курс дирхам на %s составляет %s грн";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса дирхама", e);
            formattedText = "Не удалось получить текущий курс дирхама. Попробуйте позже.";
        } catch (ServerCloneException e) {
            throw new RuntimeException(e);
        }
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту
                                
                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду";
        sendMessage(chatId, text);
    }

    private SendMessage createCurrencySelectionMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), "Выберите команду:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton usdButton = new InlineKeyboardButton("USD");
        usdButton.setCallbackData("/usd");

        InlineKeyboardButton eurButton = new InlineKeyboardButton("EUR");
        eurButton.setCallbackData("/eur");

        InlineKeyboardButton aedButton = new InlineKeyboardButton("AED");
        aedButton.setCallbackData("/aed");

        InlineKeyboardButton helpButton = new InlineKeyboardButton("HELP");
        helpButton.setCallbackData("/help");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(usdButton);
        row1.add(eurButton);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(aedButton);
        row2.add(helpButton);

        keyboard.add(row1);
        keyboard.add(row2);

        markup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(markup);

        return sendMessage;
    }

}
