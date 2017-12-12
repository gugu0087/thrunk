package com.mzj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mzj.util.MzjLog;
import com.mzj.util.TaskListHelper;

/**
 * 定时开关机接收器
 *
 * @author fyf000
 *
 */
public class TimeSwitchBroadcastReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		int id = intent.getIntExtra("id", -1); // 设置的ID
		boolean taskResult = intent.getBooleanExtra("enabled", false);
		long time = intent.getLongExtra("time", 0);
		MzjLog.i("lock","定时id："+id+"     "+time);
		if(id==1){
			MzjLog.i("lock","定时关机时间："+id+"   "+time+",设定结果是"+taskResult);
		}else{
			MzjLog.i("lock","定时开机时间："+id+"   "+time+",设定结果是"+taskResult);
		}
		TaskListHelper t = new TaskListHelper();
		t.sendTaskResultForTimeSwitch(taskResult, TaskListHelper.timeSwitch);
	}

}
