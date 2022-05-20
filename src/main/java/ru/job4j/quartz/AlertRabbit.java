package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private static final String PROPERTIES_FILE = "rabbit.properties";
    private static final String PROPERTY_SCHEDULE_TIME_INTERVAL_IN_SECONDS = "rabbit.interval";
    private static final String PROPERTY_DB_URL = "url";
    private static final String PROPERTY_DB_USERNAME = "username";
    private static final String PROPERTY_DB_PASSWORD = "password";
    private static final String PROPERTY_DB_DRIVER_CLASS_NAME = "driver-class-name";
    private static final String JOB_DATA_MAP_KEY_CONNECTION = "connection";
    private static final String QUERY_INSERT_CREATED_DATE = "insert into rabbit (created_date) values (?)";

    public static void main(String[] args) {
        try (InputStream resource = AlertRabbit.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            Properties properties = new Properties();
            properties.load(resource);
            Connection connection = getConnection(properties);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(JOB_DATA_MAP_KEY_CONNECTION, connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(jobDataMap)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(properties.getProperty(PROPERTY_SCHEDULE_TIME_INTERVAL_IN_SECONDS)))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            makeCreatedDateRecord((Connection) context.getJobDetail().getJobDataMap().get(JOB_DATA_MAP_KEY_CONNECTION));
        }

        private void makeCreatedDateRecord(Connection connection) {
            try (PreparedStatement ps = connection.prepareStatement(QUERY_INSERT_CREATED_DATE)) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Connection getConnection(Properties properties) {
        Connection connection = null;
        try {
            Class.forName(properties.getProperty(PROPERTY_DB_DRIVER_CLASS_NAME));
            connection = DriverManager.getConnection(
                    properties.getProperty(PROPERTY_DB_URL),
                    properties.getProperty(PROPERTY_DB_USERNAME),
                    properties.getProperty(PROPERTY_DB_PASSWORD)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}