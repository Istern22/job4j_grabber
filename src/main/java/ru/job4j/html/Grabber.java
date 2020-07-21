package ru.job4j.html;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {

    private final Properties config = new Properties();

    public static SqlRuParse sqlRuParse;

    public static PsqlStore psqlStore;

    public static int count = 0;

    public Store store() {
        return null;
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void config() throws IOException {
        try (InputStream in = new FileInputStream(new File("src/main/resources/store.properties"))) {
            config.load(in);
        }
    }


    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(config.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            try {
                Post post = sqlRuParse.detail(SqlRuParse.getResult().get(count).getLink());
                psqlStore.save(post);
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.config();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        Properties config = new Properties();
        config.load(PsqlStore.class.getClassLoader().getResourceAsStream("store.properties"));
        psqlStore = new PsqlStore(config);
        sqlRuParse = new SqlRuParse();
        grab.init(sqlRuParse, psqlStore, scheduler);
    }
}
