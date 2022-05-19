package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private static final String PROPERTIES_FILE = "rabbit.properties";
    private static final String SCHEDULE_TIME_INTERVAL_IN_SECONDS = "rabbit.interval";

    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(getProperty(SCHEDULE_TIME_INTERVAL_IN_SECONDS)))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }

    private static String getProperty(String keyOfProperty) {
        Properties properties = new Properties();
        try (InputStream resource = AlertRabbit.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.getProperty(keyOfProperty);
    }
}