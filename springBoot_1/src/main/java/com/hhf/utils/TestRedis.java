package com.hhf.utils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 测试redis哨兵模式
 */
public class TestRedis {

    static Random random=new Random();



    public static void main(String[] args) throws Exception{
//        SentinelMode();
        ClusterMode();
    }





    /**
     * 使用哨兵模式的时候，redis主节点挂掉之后，会在从节点里再选一个当主节点。
     * 使用原始的Jedis连接的时候，redis主节点挂掉之后，不会在从节点里再次选举。
     * @throws Exception
     */
    static void SentinelMode(){
        Set<String> hosts = new HashSet<>();
        hosts.add("www.hhf.com:26379");
        //hosts.add("127.0.0.1:36379"); 可配置多个哨兵
//        JedisSentinelPool pool = new JedisSentinelPool("myredissentinel",hosts);//哨兵模式 第一步：指定哨兵的连接(池)地址和主节点名称。
        Jedis jedis = null;
        for(int i=0 ;i<100;i++){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
//                jedis = pool.getResource();//哨兵模式 第二步：通过哨兵的连接池对象创建Jedis对象
                jedis=new Jedis("www.hhf.com",8001);//原始Jedis对象（非哨兵模式）
                jedis.auth("feixiang");
                String v = random.nextInt()+"";
                jedis.set("hello",v);
                System.out.println(v+"-->"+jedis.get("hello").equals(v));
            }catch (Exception e){
                System.out.println(" [exception happened]" + e);
            }
        }
    }

    /**
     * Jedis集群模式,实现主从节点的自动切换
     */
    static void ClusterMode(){
        JedisCluster jedisCluster=null;
        Set<HostAndPort> set=new HashSet<>();
        set.add(new HostAndPort("www.hhf.com",5000));
        set.add(new HostAndPort("www.hhf.com",5001));
        set.add(new HostAndPort("www.hhf.com",5002));
        set.add(new HostAndPort("www.hhf.com",5003));
        set.add(new HostAndPort("www.hhf.com",5004));
        set.add(new HostAndPort("www.hhf.com",5005));
        jedisCluster=new JedisCluster(set);
//        jedisCluster.set("mycluster","hhf");//set一次，然后将对应的节点kill掉。模拟主节点挂掉
        String reuslt=jedisCluster.get("mycluster");
        System.out.println(reuslt);
    }

}
