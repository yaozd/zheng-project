package common.job;

import common.Job1;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 作业任务调度器
 * Created by alan.zheng on 2017/1/16.
 */
public class JobScheduler {
    /**
     * 初始化作业.
     */
    public void init() {
//        jobExecutor.init();
//        JobTypeConfiguration jobTypeConfig = jobExecutor.getSchedulerFacade().loadJobConfiguration().getTypeConfig();
//        JobScheduleController jobScheduleController = new JobScheduleController(
//                createScheduler(jobTypeConfig.getCoreConfig().isMisfire()), createJobDetail(jobTypeConfig.getJobClass()), jobExecutor.getSchedulerFacade(), jobName);
//        jobScheduleController.scheduleJob(jobTypeConfig.getCoreConfig().getCron());
//        jobRegistry.addJobScheduleController(jobName, jobScheduleController);

        JobDetail jobDetail = JobBuilder.newJob(AbstractJob.class).withIdentity("Job1").build();
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

    /**
     * 调度作业
     */
    public static final class AbstractJob implements Job{
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            /**
             * 转换成任务具体业务类
             */
        }
    }
}
