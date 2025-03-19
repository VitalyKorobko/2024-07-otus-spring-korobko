package ru.otus.hw.services;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {

    private final EurekaClient eurekaClient;

    @Override
    public String getHostName(String serviceName) {
        return eurekaClient.getApplication(serviceName).getInstances().get(0).getHostName();
    }

    @Override
    public int getPort(String serviceName) {
        return eurekaClient.getApplication(serviceName).getInstances().get(0).getPort();
    }

}
