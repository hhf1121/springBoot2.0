//package com.hhf.config;
//
//
//import com.dangdang.ddframe.job.api.simple.SimpleJob;
//import com.dangdang.ddframe.job.config.JobCoreConfiguration;
//import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
//import com.dangdang.ddframe.job.event.JobEventConfiguration;
//import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
//import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
//import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
//import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
//import com.hhf.task.MyTask;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class ElasticJobConfig {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private MyTask myTask;
//
//    @Autowired
//    private CoordinatorRegistryCenter coordinatorRegistryCenter;
//
//    private LiteJobConfiguration createLiteJobConfiguration(Class<? extends SimpleJob> jobClass,
//                                                            String cron,
//                                                            int shardingTotalCount,
//                                                            String params){
//        //创建
//        JobCoreConfiguration.Builder builder = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount);
//        //设置params
//        if(!StringUtils.isEmpty(params)){
//            builder.shardingItemParameters(params);
//        }
//        //创建SimpleJobConfiguration
//        SimpleJobConfiguration simpleJobConfiguration=new SimpleJobConfiguration(builder.build(),jobClass.getCanonicalName());
//        //创建 LiteJobConfiguration
//        LiteJobConfiguration build = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();
//        return  build;
//    }
//
//
//
//    @Bean(initMethod = "init")
//    public SpringJobScheduler initSimpleElasticJob(){
//        //创建 JobEventConfiguration，配置数据源可监控job的运行。不配置，则不监控。
//        //自动创建两张表：job_execution_log、job_status_trace_log
//        JobEventConfiguration jobEventConfiguration=new JobEventRdbConfiguration(dataSource);
//        //创建SpringJobScheduler
//        SpringJobScheduler springJobScheduler = new SpringJobScheduler(myTask,
//                coordinatorRegistryCenter,
//                createLiteJobConfiguration(myTask.getClass(), "0 0/5 * * * ?", 3, "0=1,1=2,2=3")/*,
//                jobEventConfiguration*/);//参数jobEventConfiguration开启监控
//        return springJobScheduler;
//    }
//
//
//}
