package ru.otus.hw.repositories;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.CustomSequence;

@Repository
public class SequenceRepositoryCustomImpl implements SequenceRepositoryCustom {
    private final MongoOperations operations;

    public SequenceRepositoryCustomImpl(MongoOperations operations) {
        this.operations = operations;
    }

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
