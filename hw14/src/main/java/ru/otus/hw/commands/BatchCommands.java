package ru.otus.hw.commands;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class BatchCommands {
    private final Job toMongoMigrateJob;

    private final JobLauncher jobLauncher;

    private final MongoDatabaseFactory mongoDatabaseFactory;

    public BatchCommands(Job toMongoMigrateJob, JobLauncher jobLauncher, MongoDatabaseFactory mongoDatabaseFactory) {
        this.toMongoMigrateJob = toMongoMigrateJob;
        this.jobLauncher = jobLauncher;
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    @ShellMethod(value = "startMigrationJobToMongoDb", key = "sm")
    public void startMigrationJobWithJobLauncher() throws Exception {
        clearMongoDbIfExist();
        jobLauncher.run(toMongoMigrateJob, new JobParametersBuilder().toJobParameters());
    }

    private void clearMongoDbIfExist() {
        mongoDatabaseFactory.getMongoDatabase().drop();
    }
}

