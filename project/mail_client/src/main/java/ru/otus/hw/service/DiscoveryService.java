package ru.otus.hw.service;

public interface DiscoveryService {

    String getHostName(String serviceName);

    int getPort(String serviceName);

}
