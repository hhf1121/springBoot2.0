package com.hhf.utils;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MyReturnThread implements Callable<String>{

	@Override
	public String call() throws Exception {
		log.info("addObject----Service:Callable...");
		Thread.sleep(1000);
		return "result:service";
	}

}
