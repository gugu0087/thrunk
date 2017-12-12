package com.mzj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mzj.util.ApkInfo;
import com.mzj.util.MzjLog;

/**
 * 安装apk结果
 *
 * @author fyf000
 *
 */
public class UpdateApkResultBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = intent.getStringExtra("packageName");
		if(packageName!=null && ApkInfo.getPackageName().equals(packageName)){
			int result = intent.getIntExtra("result", 0);
			//获取安装结果
			if(result==1){
				MzjLog.i("安装成功",result+"");
			}else{
				MzjLog.i("安装成功",result+"");
			}
		}
	}
}
