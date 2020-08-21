//package com.hhf.config;
//
//import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
//import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
//import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ElasticJobZookeeperConfig {
//
//
//    //创建并初始化zk链接
//    @Bean(initMethod = "init")
//    public CoordinatorRegistryCenter initZK(){
//        //创建zk链接
//        ZookeeperConfiguration zookeeperConfiguration=new ZookeeperConfiguration("127.0.0.1:2181","myspringboot2.0-elastic-job");
//        //超时时间
//        zookeeperConfiguration.setSessionTimeoutMilliseconds(500);
//        //创建注册中心
//        CoordinatorRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
//        //初始化
////        zookeeperRegistryCenter.init();
//        return zookeeperRegistryCenter;
//    }
//
//}
