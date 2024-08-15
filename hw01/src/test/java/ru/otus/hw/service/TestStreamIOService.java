package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;

public class TestStreamIOService {
    private IOService ioService;
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;


    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream(10000);
        printStream = new PrintStream(outputStream);
        ioService = new StreamsIOService(printStream);
    }

    @Test
    void testPrintLine() {
        ioService.printLine("testing printLine method");
        printStream.flush();
        assertThat(outputStream.toString()).isEqualTo("testing printLine method\r\n");
    }

    @Test
    void testPrintFormattedLine() {
        ioService.printFormattedLine("testing%nprintFormattedLine method");
        printStream.flush();
        assertThat(outputStream.toString()).isEqualTo("testing\r\nprintFormattedLine method\r\n");
    }

}