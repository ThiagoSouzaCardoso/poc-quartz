package br.com.demo.spring.batch.pocquartz.config;

import java.io.IOException;
import java.util.Properties;

import br.com.demo.spring.batch.pocquartz.job.CustomJob;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig
{
        @Bean
        public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
            JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
            jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
            return jobRegistryBeanPostProcessor;
        }

        @Bean
        public JobDetail jobOneDetail() {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("jobName", "demoJobOne");

            return JobBuilder.newJob(CustomJob.class)
                    .withIdentity("demoJobOne",null)
                    .setJobData(jobDataMap)
                    .storeDurably()
                    .build();
        }

        @Bean
        public JobDetail jobTwoDetail() {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("jobName", "demoJobTwo");

            return JobBuilder.newJob(CustomJob.class)
                    .withIdentity("demoJobTwo",null)
                    .setJobData(jobDataMap)
                    .storeDurably()
                    .build();
        }

        @Bean
        public Trigger jobOneTrigger()
        {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInSeconds(10)
                    .repeatForever();

            return TriggerBuilder
                    .newTrigger()
                    .forJob(jobOneDetail())
                    .withIdentity("jobOneTrigger",null)
                    .withSchedule(scheduleBuilder)
                    .build();
        }

        @Bean
        public Trigger jobTwoTrigger()
        {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInSeconds(20)
                    .repeatForever();

            return TriggerBuilder
                    .newTrigger()
                    .forJob(jobTwoDetail())
                    .withIdentity("jobTwoTrigger",null)
                    .withSchedule(scheduleBuilder)
                    .build();
        }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException, SchedulerException
    {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(jobOneTrigger(), jobTwoTrigger());
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setJobDetails(jobOneDetail(), jobTwoDetail());
        scheduler.setApplicationContextSchedulerContextKey("applicationContext");
        return scheduler;
    }

    public Properties quartzProperties() throws IOException
    {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}