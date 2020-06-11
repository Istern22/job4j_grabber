package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    private Connection cn;

    public void init() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("app.properties")) {
           Properties config = new Properties();
           config.load(in);
           Class.forName(config.getProperty("jdbc.driver"));
           cn = DriverManager.getConnection(
                   config.getProperty("jdbc.url"),
                   config.getProperty("jdbc.username"),
                   config.getProperty("jdbc.password")
           );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(LocalDate date) {
        try (PreparedStatement preparedStatement = cn.prepareStatement(
                "INSERT INTO rabbit (created) VALUES (?)")) {
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AlertRabbit rabbit = new AlertRabbit();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            int interval = Integer.parseInt(config.getProperty("rabbit.interval"));
            try {
                List<Long> store = new ArrayList<>();
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("store", store);
                JobDetail job = newJob(Rabbit.class).usingJobData(data).build();
                SimpleScheduleBuilder times = simpleSchedule().withIntervalInSeconds(interval).repeatForever();
                Trigger trigger = newTrigger().startNow().withSchedule(times).build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
                rabbit.init();
                rabbit.add(LocalDate.now());
            } catch (Exception se) {
                se.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());
        }
    }
}
