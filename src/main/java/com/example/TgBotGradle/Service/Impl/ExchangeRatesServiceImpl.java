package com.example.TgBotGradle.Service.Impl;

import com.example.TgBotGradle.Client.UaClient;
import com.example.TgBotGradle.Exception.ServiceException;
import com.example.TgBotGradle.Service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.rmi.server.ServerCloneException;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    public static final String USD_XPATH = "/exchange/currency[cc='USD']/rate";
    public static final String EUR_XPATH = "/exchange/currency[cc='EUR']/rate";
    @Autowired
    private UaClient client;
    @Override
    public String getUSDExchangeRate() throws SecurityException, ServiceException, ServerCloneException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml,USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws SecurityException, ServiceException, ServerCloneException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml,EUR_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServerCloneException {
        var sourse = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newDefaultInstance().newXPath();
            var document = (org.w3c.dom.Document) xpath.evaluate("/", sourse, XPathConstants.NODE);
            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServerCloneException("Не удалось распарсить XML", e);
        }
    }
}
