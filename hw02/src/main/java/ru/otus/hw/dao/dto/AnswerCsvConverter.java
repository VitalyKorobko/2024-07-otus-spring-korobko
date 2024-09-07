package ru.otus.hw.dao.dto;

import com.opencsv.bean.AbstractCsvConverter;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Objects;

public class AnswerCsvConverter extends AbstractCsvConverter {

    @Override
    public Object convertToRead(String value) {
        if (isEmpty(value)) {
            return null;
        }
        var valueArr = value.split("%");
        checkAnswer(valueArr);
        return new Answer(valueArr[0], Boolean.parseBoolean(valueArr[1]));
    }

    private static void checkAnswer(String[] valueArr) {
        if (valueArr.length != 2) {
            throw new QuestionReadException("incorrect answer, cannot convert answer");
        }
    }

    private static boolean isEmpty(String value) {
        return Objects.isNull(value) || value.isBlank();
    }


}
