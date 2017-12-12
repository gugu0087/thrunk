package com.mzj.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import com.mzj.scale_app.DeskActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;


/**
 * app关机重启控制类
 * @author fyf000
 *
 */
public class ControlAppUtil {

	public static String controlDeviceStatus;
	public static WakeLock wakeLockForShutDown = null;

	public ControlAppUtil(){

	}
	private Activity activity;
	private PowerManager mPowerManager;
	public ControlAppUtil(Activity activity){
		this.activity = activity;
		mPowerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
	}
//	 public static void acquireWakeLock(Activity activity)
//	    {
//	        if (null == wakeLockForShutDown)
//	        {
//	            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
//	            wakeLockForShutDown = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakeLockUtil");
//	            // PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的 -- 最常用,保持CPU运转
//	            // SCREEN_DIM_WAKE_LOCK：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
//	            // SCREEN_BRIGHT_WAKE_LOCK：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
//	            // FULL_WAKE_LOCK：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
//	            // ACQUIRE_CAUSES_WAKEUP：强制使屏幕亮起，这种锁主要针对一些必须通知用户的操作.
//	            // ON_AFTER_RELEASE：当锁被释放时，保持屏幕亮起一段时间
//	            if (null != wakeLockForShutDown)
//	            {
//	            	wakeLockForShutDown.acquire(); // 立即获取电源锁
//	                // wakeLock.acquire(2000); // 2秒后获取电源锁
//	            }
//	        }
//	    }
//
//	 public static void releaseWakeLock()
//	    {
//	        if (null != wakeLockForShutDown)
//	        {
//	        	wakeLockForShutDown.release();
//	        	wakeLockForShutDown = null;
//	        }
//	    }

	/**
	 * 控制app光机开机
	 *
	 * @param controlDeviceStatus
	 * @param time
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void controlApp(String controlDeviceStatus) throws InterruptedException, IOException {
		MzjLog.i("1111", new Date().getTime()+""+"   "+controlDeviceStatus);

		ControlAppUtil.controlDeviceStatus = controlDeviceStatus;
		if (controlDeviceStatus == null || controlDeviceStatus.equals("")) {
			return;
		}

		AudioManagerUtil cudioManagerUtil = new AudioManagerUtil(this.activity);
		//定时唤醒
		switch (controlDeviceStatus) {

			case Commons.controlType_timingStart:

				DeskActivity.setVolumeUp();
//			cudioManagerUtil.OpenSpeaker();
				MzjLog.i("22222", new Date().getTime()+""+"   "+controlDeviceStatus);
//			releaseWakeLock();

				KeyguardManager km= (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
				KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
				//解锁
				kl.disableKeyguard();
				//获取电源管理器对象
				PowerManager pm=(PowerManager) activity.getSystemService(Context.POWER_SERVICE);
				if(pm.isScreenOn() == false){
					MzjLog.i("333", new Date().getTime()+""+"   "+controlDeviceStatus);
					//获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
					WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
					//点亮屏幕
					wl.acquire();
					//释放
					wl.release();
				}
				break;

			case Commons.controlType_timingShutdown:
				//定时休眠
			case Commons.controlType_nowShutdown:
				//立即休眠
				DeskActivity.setVolumeDown();
				mPowerManager.goToSleep(SystemClock.uptimeMillis());
//			 acquireWakeLock(activity);

//			cudioManagerUtil.CloseSpeaker();
				break;

			case Commons.controlType_timingReboot:
				//定时重启
			case Commons.controlType_nowReboot:

				//立即重启
				try {
					createSuProcess("reboot").waitFor();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}



	public static Process createSuProcess() throws IOException {
		File rootUser = new File("/system/xbin/ru");
		if (rootUser.exists()) {
			return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
		} else {
			return Runtime.getRuntime().exec("su");
		}
	}

	public static Process createSuProcess(String cmd) throws IOException {
		DataOutputStream os = null;
		Process process = createSuProcess();
		try {
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit $?\n");
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
		return process;
	}


	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean rootCommand(String command)
	{
		Process process = null;
		DataOutputStream os = null;
		try
		{
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e)
		{
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally
		{
			try
			{
				if (os != null)
				{
					os.close();
				}
				process.destroy();
			} catch (Exception e)
			{
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

	/**
	 * 执行具体的静默安装逻辑，需要手机ROOT。
	 * @param apkPath
	 *          要安装的apk文件的路径
	 * @return 安装成功返回true，安装失败返回false。
	 */
	public static boolean install(String apkPath) {

		MzjLog.i("开始安装apk", new Date().getTime()+"");
		File  file = new File(apkPath);
		if(!file.exists()){
			return false;
		}

		boolean result = false;
		DataOutputStream dataOutputStream = null;
		BufferedReader errorStream = null;
		try {
			// 申请su权限
			Process process = createSuProcess();
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			// 执行pm install命令
			String command = "pm install -r " + apkPath + "\n";
			dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
			dataOutputStream.flush();
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
			errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String msg = "";
			String line;
			// 读取命令的执行结果
			while ((line = errorStream.readLine()) != null) {
				msg += line;
			}
			Log.d("TAG", "install msg is " + msg);
			// 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
			if (!msg.contains("Failure")) {
				result = true;
			}
		} catch (Exception e) {
			Log.e("TAG", e.getMessage(), e);
		} finally {
			try {
				MzjLog.i("开始安装apk结束", new Date().getTime()+"");
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				if (errorStream != null) {
					errorStream.close();
				}
			} catch (IOException e) {
				Log.e("TAG", e.getMessage(), e);
			}
		}
		return result;
	}

}