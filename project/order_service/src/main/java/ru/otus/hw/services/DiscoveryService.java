package ru.otus.hw.services;

public interface DiscoveryService {

    String getHostName(String serviceName);

    int getPort(String serviceName);

}
