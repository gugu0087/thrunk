package com.mzj.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);  
	public void handelTask(){
		 Runnable runnable = new Runnable(){  
             @Override  
             public void run() {  
            	 
             }  
         }; 
         fixedThreadPool.execute(runnable);
	}
}
