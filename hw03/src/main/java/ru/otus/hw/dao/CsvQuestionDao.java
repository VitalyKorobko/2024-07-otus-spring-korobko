package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<QuestionDto> dtoList = createListQuestionDto();
        return createListQuestion(dtoList);
    }

    private List<Question> createListQuestion(List<QuestionDto> dtoList) {
        return dtoList.stream()
                .map(QuestionDto::toDomainObject)
                .toList();
    }


    private List<QuestionDto> createListQuestionDto() {
        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(fileNameProvider.getTestFileName())) {
            if (Objects.nonNull(inputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                    return new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                            .withSkipLines(1)
                            .withSeparator(';')
                            .withType(QuestionDto.class)
                            .build()
                            .parse();
                }
            }
        } catch (IOException e) {
            throw new QuestionReadException("Impossible to read questions DTO", e);
        }
        throw new QuestionReadException("file not found");
    }

}
