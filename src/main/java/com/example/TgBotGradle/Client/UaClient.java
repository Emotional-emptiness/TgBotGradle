package com.example.TgBotGradle.Client;

import com.example.TgBotGradle.Exception.ServiceException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UaClient {
    @Autowired
    private OkHttpClient client;

    @Value("${ua.currency.rates.xml.url}")
    private String url;

    public String getCurrencyRatesXML() throws ServiceException {
        var request = new Request.Builder()
                .url(url)
                .build();
        try (var response = client.newCall(request).execute();) {
            var body = response.body();
            return body == null ? null : body.string();
        } catch (IOException e) {
            throw new ServiceException("Ошибка получения курса валют ", e);
        }
    }
}
