package spiservice.job;

import common.utility.PropertiesUtility;
import domain.manager.JobManager;
import domain.model.Job.Job;
import domain.model.Job.JobLog;
import domain.model.Job.query.JobLogQuery;
import domain.model.Job.query.JobQuery;
import domain.model.PageModel;
import job.config.JobCommand;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reg.zookeeper.ZookeeperConfig;
import reg.zookeeper.ZookeeperRegistryCenter;
import spi.job.JobSPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alan.zheng on 2017/1/18.
 */
@Service("jobSPIService")
public class JobSPIService implements JobSPI {
    @Autowired
    private JobManager jobManager;

    public Job queryById(Long jobId) {
        Job job=jobManager.queryById(jobId);
        if (job==null){
            return null;
        }
        ZookeeperConfig zookeeperConfig=new ZookeeperConfig();
        PropertiesUtility propertiesUtility=new PropertiesUtility("zookeeper.properties");
        zookeeperConfig.setServerLists(propertiesUtility.getProperty("zk.serverList"));
        zookeeperConfig.setNamespace(propertiesUtility.getProperty("zk.namespace"));
        zookeeperConfig.setAuth(propertiesUtility.getProperty("zk.auth"));
        ZookeeperRegistryCenter zookeeperRegistryCenter= null;
        try {
            zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        zookeeperRegistryCenter.init();
        if (zookeeperRegistryCenter.isExisted("/"+job.getJobName()+"")){
            job.setStatus(1);
        }else {
            job.setStatus(-1);
        }
        zookeeperRegistryCenter.close();
        return job;
    }

    public List<Job> queryList() {
        List<Job> jobs= jobManager.queryList();
        ZookeeperConfig zookeeperConfig=new ZookeeperConfig();
        PropertiesUtility propertiesUtility=new PropertiesUtility("zookeeper.properties");
        zookeeperConfig.setServerLists(propertiesUtility.getProperty("zk.serverList"));
        zookeeperConfig.setNamespace(propertiesUtility.getProperty("zk.namespace"));
        zookeeperConfig.setAuth(propertiesUtility.getProperty("zk.auth"));
        ZookeeperRegistryCenter zookeeperRegistryCenter= null;
        try {
            zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        zookeeperRegistryCenter.init();
        if (jobs!=null&&jobs.size()>0){
            for (Job job:jobs) {
                if (zookeeperRegistryCenter.isExisted("/"+job.getJobName()+"")){
                    job.setStatus(1);
                }else {
                    job.setStatus(-1);
                }
            }
        }
        zookeeperRegistryCenter.close();
        return jobs;
    }

    public PageModel<Job> queryPageList(JobQuery query) {
        PageModel pageModel=jobManager.queryPageList(query);
        if (pageModel!=null&&pageModel.getModel()!=null&&pageModel.getModel().size()>0){
            ZookeeperConfig zookeeperConfig=new ZookeeperConfig();
            PropertiesUtility propertiesUtility=new PropertiesUtility("zookeeper.properties");
            zookeeperConfig.setServerLists(propertiesUtility.getProperty("zk.serverList"));
            zookeeperConfig.setNamespace(propertiesUtility.getProperty("zk.namespace"));
            zookeeperConfig.setAuth(propertiesUtility.getProperty("zk.auth"));
            ZookeeperRegistryCenter zookeeperRegistryCenter= null;
            try {
                zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            zookeeperRegistryCenter.init();
            for (int i=0;i<pageModel.getModel().size();i++){
                Job job=(Job) pageModel.getModel().get(i);
                if (zookeeperRegistryCenter.isExisted("/"+job.getJobName()+"")){
                    String zkvalue= zookeeperRegistryCenter.get("/"+job.getJobName()+"");
                    if (JobCommand.PAUSE.getCommand().equals(zkvalue)){
                        job.setStatus(-2);
                    }else {
                        job.setStatus(1);
                    }
                }else {
                    job.setStatus(-1);
                }
            }
            zookeeperRegistryCenter.close();
        }
        return pageModel;
    }

    public boolean insertJob(Job job) {
        return jobManager.insertJob(job);
    }

    public PageModel<JobLog> queryPageJobLog(JobLogQuery query) {
        PageModel<JobLog> jobLogPageModel= jobManager.queryJobLogList(query);
        return jobLogPageModel;
    }

    public void jobCommand(Long jobId, JobCommand command) {
        Job job= jobManager.queryById(jobId);
        if (job!=null){
            ZookeeperConfig zookeeperConfig=new ZookeeperConfig();
            PropertiesUtility propertiesUtility=new PropertiesUtility("zookeeper.properties");
            zookeeperConfig.setServerLists(propertiesUtility.getProperty("zk.serverList"));
            zookeeperConfig.setNamespace(propertiesUtility.getProperty("zk.namespace"));
            zookeeperConfig.setAuth(propertiesUtility.getProperty("zk.auth"));
            ZookeeperRegistryCenter zookeeperRegistryCenter= null;
            try {
                zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            zookeeperRegistryCenter.init();
            if (zookeeperRegistryCenter.isExisted("/"+job.getJobName()+"")){
                String zkvalue = zookeeperRegistryCenter.get("/"+job.getJobName()+"");
                zookeeperRegistryCenter.update("/"+job.getJobName()+"",command.getCommand());
                if (JobCommand.EXECUTE.equals(command)){
                    zookeeperRegistryCenter.update("/"+job.getJobName()+"",zkvalue);
                }
                if (JobCommand.SHUTDOWN.equals(command)){
                    zookeeperRegistryCenter.remove("/"+job.getJobName());
                }
            }
            zookeeperRegistryCenter.close();
        }
    }

    public boolean deleteJob(Long jobId) {
        return jobManager.deleteJob(jobId);
    }
}
