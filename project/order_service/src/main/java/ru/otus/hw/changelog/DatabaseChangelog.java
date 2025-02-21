package ru.otus.hw.changelog;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;

@ChangeLog(order = "001")
@RequiredArgsConstructor
public class DatabaseChangelog {

    @ChangeSet(order = "000", id = "dropDb", author = "korobko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }



}
