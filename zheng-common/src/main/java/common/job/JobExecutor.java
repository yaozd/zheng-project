package common.job;

import common.reg.base.AbstractListener;
import common.reg.zookeeper.ZookeeperConfig;
import common.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import java.util.Date;

/**
 * 任务启动类
 * Created by alan.zheng on 2017/1/17.
 */
public abstract class JobExecutor {
    private final JobConfig jobConfig;
    public JobExecutor(JobConfig _jobConfig){
        jobConfig=_jobConfig;
    }
    /**
     * 任务启动入口方法
     */
    protected void excute(){
        /**
         * 开始前置处理，(开始进行zk监听处理)
         */
        ZookeeperConfig zookeeperConfig=new ZookeeperConfig();
        zookeeperConfig.setServerLists("127.0.0.1:2181");
        zookeeperConfig.setNamespace("root");
        zookeeperConfig.setAuth("auth");
        ZookeeperRegistryCenter zookeeperRegistryCenter= null;
        try {
            zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        zookeeperRegistryCenter.init();
        CuratorFramework curatorFramework=(CuratorFramework) zookeeperRegistryCenter.getRawClient();
        TreeCache treeCache=new TreeCache(curatorFramework,"/"+jobConfig.getJobName()+"");

        treeCache.getListenable().addListener(new AbstractListener() {
            @Override
            public void changed(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) {
                System.out.print("发生了监听:"+new Date().toString());
            }
        });
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //启动任务时候插入zk临时任务数据
        if (!zookeeperRegistryCenter.isExisted("/"+jobConfig.getJobName()+"")){
            zookeeperRegistryCenter.createEphemeral("/"+jobConfig.getJobName()+"",jobConfig.getJobName());
        }
        process();
    }

    /**
     * 任务业务程序处理
     */
    protected abstract void process();
}