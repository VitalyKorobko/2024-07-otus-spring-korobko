package ru.otus.hw.commands;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class BatchCommands {
    private final Job toMongoMigrateJob;

    private final JobLauncher jobLauncher;

    public BatchCommands(Job toMongoMigrateJob, JobLauncher jobLauncher) {
        this.toMongoMigrateJob = toMongoMigrateJob;
        this.jobLauncher = jobLauncher;
    }

    @ShellMethod(value = "startMigrationJobToMongoDb", key = "sm")
    public void startMigrationJobWithJobLauncher() throws Exception {
        jobLauncher.run(toMongoMigrateJob, new JobParametersBuilder().toJobParameters());
    }

}

