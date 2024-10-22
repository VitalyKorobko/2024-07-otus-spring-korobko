package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.enums.Seq;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для получения id ")
@DataMongoTest
@Import({IdSequencesServiceImpl.class})
public class IdSequenceServiceImplTest {
    private static final String FOURTH_COMMENT_ID = "4";

    private static final String FOURTH_BOOK_ID = "4";

    @Autowired
    private IdSequencesServiceImpl idSequencesService;

    @Test
    @DisplayName(" должен возвращать id для комментария")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldReturnCommentId(){
        var returnedID = idSequencesService.getNextId(Seq.COMMENT.getSeqName());
        assertThat(returnedID).isNotNull().isEqualTo(FOURTH_COMMENT_ID);
    }

    @Test
    @DisplayName(" должен возвращать id для книги")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldReturnBookId(){
        var returnedID = idSequencesService.getNextId(Seq.BOOK.getSeqName());
        assertThat(returnedID).isNotNull().isEqualTo(FOURTH_BOOK_ID);
    }



}
