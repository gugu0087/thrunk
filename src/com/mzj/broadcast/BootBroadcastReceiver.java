package com.mzj.broadcast;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mzj.scale_app.MainActivity;


/**
 * 开机自启动
 * @author fyf000
 *
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
		if(keyguardManager.isKeyguardLocked()){
			keyguardManager.newKeyguardLock("").disableKeyguard();//解锁
		}
		Intent intentStart = new Intent(context,MainActivity.class);
		intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intentStart);

	}


}
