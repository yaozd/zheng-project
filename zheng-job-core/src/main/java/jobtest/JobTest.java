package jobtest;

import job.JobScheduleController;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Created by XR on 2016/12/22.
 */
public class JobTest {
    public static void main(String[] args){
        JobDetail jobDetail = JobBuilder.newJob(Job1.class).withIdentity("Job1").build();
        jobDetail.getJobDataMap().put("jobconfig","看看情况");
        Scheduler scheduler=null;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            factory.initialize();
            scheduler = factory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        JobScheduleController jobScheduleController=new JobScheduleController(scheduler,jobDetail,"t1");
        jobScheduleController.scheduleJob("0/1 * * * * ?");
    }
}


