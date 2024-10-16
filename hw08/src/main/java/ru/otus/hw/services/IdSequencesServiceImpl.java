package ru.otus.hw.services;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.CustomSequence;

@Service
public class IdSequencesServiceImpl implements IdSequencesService {
    private final MongoOperations operations;

    public IdSequencesServiceImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Transactional(readOnly = true)
    @Override
    public String getNextId(String seqName) {
        CustomSequence counter = operations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                CustomSequence.class);
        return String.valueOf(counter.getSeq());
    }

}

