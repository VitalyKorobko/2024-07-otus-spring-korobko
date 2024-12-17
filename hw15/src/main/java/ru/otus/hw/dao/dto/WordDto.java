package ru.otus.hw.dao.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class WordDto {
    @CsvBindByPosition(position = 0)
    private String word;

    public String toDomainObject() {
        return word;
    }
}
