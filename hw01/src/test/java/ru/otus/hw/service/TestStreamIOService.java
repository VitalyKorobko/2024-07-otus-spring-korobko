package ru.otus.hw.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TestStreamIOService {
    private StreamsIOService ioService;

    @BeforeEach
    void setUp() {
        ioService = mock(StreamsIOService.class);
    }


    @Test
    void testPrintLine() {
        String str = "testing printLine method";
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(ioService).printLine(valueCapture.capture());
        ioService.printLine(str);
        Assertions.assertThat(valueCapture.getValue()).isEqualTo(str);
        verify(ioService, times(1)).printLine(str);
    }

    @Test
    void testPrintFormattedLine() {
        String str = "testing%nprintFormattedLine method";
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(ioService).printFormattedLine(valueCapture.capture());
        ioService.printFormattedLine(str);
        Assertions.assertThat(valueCapture.getValue()).isEqualTo(str);
        verify(ioService, times(1)).printFormattedLine(str);
    }

}