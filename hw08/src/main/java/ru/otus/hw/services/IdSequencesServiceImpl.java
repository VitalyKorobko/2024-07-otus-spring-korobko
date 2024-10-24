package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.SequenceRepositoryCustom;

@RequiredArgsConstructor
@Service
public class IdSequencesServiceImpl implements IdSequencesService {
    private final SequenceRepositoryCustom sequenceRepositoryCustom;

    @Override
    public String getNextId(String seqName) {
        return sequenceRepositoryCustom.getNextId(seqName);
    }

}

