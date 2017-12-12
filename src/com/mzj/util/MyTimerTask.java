package com.mzj.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimerTask {

	private Timer timer = null;
	private TimerTask timerTask = null;
	private Handler handler;
	private int delay;
	private int what;
	private Object obj;


	public MyTimerTask() {

	}

	public MyTimerTask(Handler handler,int delay,int what) {
		this.handler = handler;
		this.delay = delay;
		this.what = what;
	}


	public MyTimerTask(Handler handler,int delay,int what,Object obj) {
		this.handler = handler;
		this.delay = delay;
		this.what = what;
		this.obj = obj;
	}



	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * 开始任务
	 */

	public synchronized void startTimer() {
		if (timer == null) {
			timer = new Timer();
		}

		if (timerTask == null) {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = what;
					msg.obj = obj;
					handler.sendMessage(msg);
				}
			};
		}

		if (timer != null && timerTask != null)
			timer.schedule(timerTask, delay);

	}

	/**
	 * 停止任务
	 */
	public synchronized void stopTimer() {


		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}


	/**
	 * 停止任务
	 */
	public void stopTimerTask() {

		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}


	public void addTimeTask(int delay){
		stopTimerTask();
		this.delay = delay;
		startTimer();
	}

}
