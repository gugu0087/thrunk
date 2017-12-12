package com.mzj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mzj.scale_app.MainActivity;
import com.mzj.util.MzjLog;

/**
 *更新apk自启动
 *
 * @author fyf000
 *
 */
public class UpdateBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		MzjLog.i("更新apk成功","更新apk成功");
		Intent intent2 = new Intent(context, MainActivity.class);
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2);
	}

}
