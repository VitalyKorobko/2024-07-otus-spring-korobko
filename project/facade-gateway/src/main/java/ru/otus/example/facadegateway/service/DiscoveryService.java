package ru.otus.example.facadegateway.service;

public interface DiscoveryService {

    String getHostName(String serviceName);

    int getPort(String serviceName);

}
