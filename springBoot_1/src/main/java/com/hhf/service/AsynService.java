package com.hhf.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hhf.utils.MyReturnThread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsynService {

	//新增。
	@Async//相当于此方法单独开了一个线程
	public String addObjet() throws InterruptedException, ExecutionException {
		log.info("addObject----Service：原始...");
		Thread.sleep(1000);
		return "result:service";
		
		
		//@Async相当于：
		//1.普通Thread
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				//逻辑代码...
//				log.info("addObject----Service:Runable...");
//				//return "service:sleep->1s";//需要返回值的Thread，可用Callable
//			}
//		}).start();
//		return "result:service";
		// 2.创建一个线程池
//        ExecutorService pool = Executors.newFixedThreadPool(1);
//        MyReturnThread task = new MyReturnThread();
//        Future<String> submit = pool.submit(task);
//        return submit.get();
		
	}
}
