package ru.otus.hw.mongock.changelog;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.model.Quantity;
import ru.otus.hw.repository.QuantityRepository;

import java.util.List;
import java.util.UUID;

@ChangeLog(order = "001")
@RequiredArgsConstructor
public class DatabaseChangelog {
    @ChangeSet(order = "000", id = "dropDb", author = "korobko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "001", id = "2025-02-13-001-products", author = "korobko")
    public void insertQuantity(QuantityRepository quantityRepository) {
        List<Quantity> quantities = List.of(
                new Quantity(
                        UUID.randomUUID().toString(),
                        500,
                        "1"),
                new Quantity(
                        UUID.randomUUID().toString(),
                        1000,
                        "2"),
                new Quantity(
                        UUID.randomUUID().toString(),
                        2000,
                        "3")
        );
        quantityRepository.saveAll(quantities).blockLast();
    }

}
