package com.example.TgBotGradle.Service;

import com.example.TgBotGradle.Exception.ServiceException;

import java.rmi.server.ServerCloneException;

public interface ExchangeRatesService {
    String getUSDExchangeRate() throws SecurityException, ServiceException, ServerCloneException;

    String getEURExchangeRate() throws SecurityException, ServiceException, ServerCloneException;

    String getAEDExchangeRate() throws SecurityException, ServiceException, ServerCloneException;
}
