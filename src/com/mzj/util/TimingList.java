package com.mzj.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时容器类
 *
 * @author fyf000
 *
 */
public class TimingList {

	// 存放定时器的列表
	private static List<NewTimer>  timerList = new ArrayList<NewTimer>();

	/**
	 * 清除所有定时器
	 */
	public void clearAllTimeList() {
		if (timerList != null) {
			for (int i = 0; i < timerList.size(); i++) {
				NewTimer timer = timerList.get(i);
				if (timer.getTimerTask() != null) {
					timer.getTimerTask().cancel();
				}
				if (timer != null) {
					timer.cancel();
				}
			}
		}
	}

	/**
	 * 定时内部类
	 *
	 * @author fyf000
	 *
	 */
	class NewTimer extends Timer {

		private String timerId; // 后台的定时任务id，返回给后台用的
		private String runTime; // 运行时间
		private String controlType; // 控制类型（1：定时开机，2：定时关机3：定时重启
		private String runType; // 执行类型 0 一次 1 每日
		private TimerTask timerTask;

		public String getTimerId() {
			return timerId;
		}

		public void setTimerId(String timerId) {
			this.timerId = timerId;
		}

		public String getRunTime() {
			return runTime;
		}

		public void setRunTime(String runTime) {
			this.runTime = runTime;
		}

		public String getControlType() {
			return controlType;
		}

		public void setControlType(String controlType) {
			this.controlType = controlType;
		}

		public String getRunType() {
			return runType;
		}

		public void setRunType(String runType) {
			this.runType = runType;
		}

		public TimerTask getTimerTask() {
			return timerTask;
		}

		public void setTimerTask(TimerTask timerTask) {
			this.timerTask = timerTask;
		}

		protected NewTimer(String timerId, String runTime, String controlType,
						   String runType) {

		}
	}
}
