package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dao.dto.WordDto;
import ru.otus.hw.exception.VocabularyReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CsvVocabularyDao implements VocabularyDao {

    @Override
    public List<String> findAll(String vocabularyPath) {
        List<WordDto> dtoList = createVocabulary(vocabularyPath);
        return createListQuestion(dtoList);
    }

    private List<String> createListQuestion(List<WordDto> dtoList) {
        return dtoList.stream()
                .map(WordDto::toDomainObject)
                .toList();
    }


    private List<WordDto> createVocabulary(String vocabularyPath) {
        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(vocabularyPath)) {
            if (Objects.nonNull(inputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                    return new CsvToBeanBuilder<WordDto>(inputStreamReader)
                            .withSkipLines(0)
//                            .withSeparator(';')
                            .withType(WordDto.class)
                            .build()
                            .parse();
                }
            }
        } catch (IOException e) {
            throw new VocabularyReadException("Impossible to read vocabulary DTO", e);
        }
        throw new VocabularyReadException("file not found");
    }

}
