package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        var inputStream = ClassLoader.getSystemResourceAsStream(fileNameProvider.getTestFileName());
        if (inputStream != null) {
            var dtoList = new CsvToBeanBuilder<QuestionDto>(new InputStreamReader(inputStream))
                    .withSkipLines(1)
                    .withSeparator(';')
                    .withType(QuestionDto.class)
                    .build()
                    .parse();
            return dtoList.stream()
                    .parallel()
                    .map(x -> new Question(x.getText(), x.getAnswers()))
                    .filter(x -> x.text() != null && x.answers() != null)
                    .collect(ArrayList::new, List::add, List::addAll);
        } else {
            throw new QuestionReadException("Impossible to read questions");
        }

    }
}
