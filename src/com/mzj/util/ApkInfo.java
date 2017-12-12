package com.mzj.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mzj.crashHandler.CrashApplication;

public class ApkInfo {

	private static PackageInfo info;
	public ApkInfo(){
		PackageManager  manager = CrashApplication.getAppContext().getPackageManager();
		try{
			info = manager.getPackageInfo(CrashApplication.getAppContext().getPackageName(), 0);
		}catch(NameNotFoundException e){
			e.printStackTrace();
		}
	}


	/**
	 * 获取apk版本号
	 * @return
	 */
	public static String getApkInfoVersionCode(){
		return info.versionCode+"";
	}


	/**
	 * 获取项目包名
	 * @return
	 */
	public static String getPackageName(){
		return info.packageName;
	}
}
