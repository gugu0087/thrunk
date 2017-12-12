package com.mzj.scale_app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;

import com.mzj.bean.Weight;
import com.mzj.broadcast.TimeSwitchBroadcastReceiver;
import com.mzj.broadcast.UpdateApkResultBroadcastReceiver;
import com.mzj.crashHandler.CrashActivityList;
import com.mzj.util.HttpGetData;
import com.mzj.util.MzjLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 主页面辅助类
 *
 * @author Administrator
 *
 */
public class DeskActivityHelper {

	public static String NetType = "";
	public static String modal = "";
	public static String sign = "";

	public static String NetTypeOld = "";
	public static String modalOld = "";
	public static String signOld = "";

	private Activity activity;
	private Handler handler;

	public DeskActivityHelper(Activity activity, Handler handler) {
		this.activity = activity;
		this.handler = handler;
	}

	public void init() {
		CrashActivityList.addAcitivity(activity);
//		monitorWife();// 监听wife
//		monitorMobile();// 监听sim卡
//		screenModify();// 屏幕亮度手工调节模式
//		registReInstallApkReceiver();// 注册重新安装apk结果接收器
//		registTimeSwitchReceiver();// 注册定时开关机结果接收器
		hideNavigationBar();// 隐藏下面的bar
//		sendWeightTiming();//定时把没有发送到服务器的体重发送到服务器
	}

	/**
	 * 监听wife
	 */
	public void monitorWife() {
		BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int s = obtainWifiInfo();
				Message msg = new Message();
				msg.what = 19;
				msg.obj = s;
				handler.sendMessage(msg);
			}
		};
		activity.registerReceiver(rssiReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
	}

	/**
	 * wifi 监听会调函数
	 *
	 * @return
	 */
	private int obtainWifiInfo() {
		// Wifi的连接速度及信号强度：
		int level = 0;
		WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info.getBSSID() != null) {
			// 链接信号强度，5为获取的信号强度值在5以内
			// 到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线，一般Wifi已断则值为-200。
			// strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
			level = info.getRssi();
			MzjLog.i("wifi", level + "");
//			sign = level + "";
			NetType = "wifi";

			if(level==0){
				sign = "0";
			}else if (level < 0 && level >= -50) {
				sign = "4";
			} else if (level < -50 && level >= -70) {
				sign = "3";
			} else if (level < -70 && level >= -80) {
				sign = "2";
			} else if (level < -80 && level >= -100) {
				sign = "1";
			} else {
				sign = "0";
			}
		}
		return level;
	}

	// 监听sim卡信号
	private void monitorMobile() {

		// sim卡信号管理器
		final TelephonyManager telephoneManager = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener phoneStateListener = new PhoneStateListener() {

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				super.onSignalStrengthsChanged(signalStrength);
//				int level = signalStrength.getLevel();
				int level = 0;
				StringBuffer sb = new StringBuffer();
				String strength = String.valueOf(signalStrength.getGsmSignalStrength());
				int type = telephoneManager.getNetworkType();
				String singType = null;
				if (type == 4 || type == 16) {
					singType = "2g";
				} else if (type == 17) {
					singType = "3g";
				} else {
					singType = "4g";
				}

				NetType = "mobile";
				modal = type + "";
				sign = level + "";

				MzjLog.i("sim", singType);
				MzjLog.i("信號級別", level + "");
				String imsi = telephoneManager.getSubscriberId();
				int imsiMoboile = 0;
				if (imsi != null) {
					if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46004")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
						MzjLog.i("中国移动", "中国移动");
						imsiMoboile = 0;
						// 中国移动
					} else if (imsi.startsWith("46001")) {
						MzjLog.i("中国电信", "中国电信");
						imsiMoboile = 1;
						// 中国联通
					} else if (imsi.startsWith("46003")) {
						MzjLog.i("中国电信", "中国电信");
						imsiMoboile = 2;
						// 中国电信
					} else {
						MzjLog.i("供應商", "其他");
					}
				}

				if (NetType.equals(NetTypeOld) && modal.equals(modalOld) && sign.equals(signOld)) {

				} else {
					NetTypeOld = NetType;
					modalOld = modal;
					signOld = sign;

					Message msg = new Message();
					msg.what = 24;
					Map<String, String> map = new HashMap<String, String>();
					map.put("NetType", NetType);
					map.put("modal", modal);
					map.put("sign", sign);
					msg.obj = map;
					handler.sendMessage(msg);
				}
			}

		};
		telephoneManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	// 屏幕亮度手工调节模式
	public void screenModify() {
		try {
			if (Settings.System.getInt(activity.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
				Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
						Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			}
		} catch (SettingNotFoundException e) {
		}
	}

	// 注册重新安装apk结果接收器
	public void registReInstallApkReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("duoyue.intent.action.INSTALL_PACKAGE_COMPLETE");
		filter.addAction("duoyue.intent.action.INSTALL_PACKAGE_TIMEOUT");
		UpdateApkResultBroadcastReceiver uarbrReceiver = new UpdateApkResultBroadcastReceiver();
		activity.registerReceiver(uarbrReceiver, filter);
	}

	// 注册定时开关机结果接收器
	public void registTimeSwitchReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.android.settings.timerpower.SET_ALARM_RESULT");
		TimeSwitchBroadcastReceiver tsbr = new TimeSwitchBroadcastReceiver();
		activity.registerReceiver(tsbr, filter);
	}

	// 隐藏屏幕下面的bar
	public void hideNavigationBar() {
		int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
				// nav
				// bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

		if (android.os.Build.VERSION.SDK_INT >= 19) {
			uiFlags |= 0x00001000; // SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide
			// navigation bars - compatibility: building
			// API level is lower thatn 19, use magic
			// number directly for higher API target
			// level
		} else {
			uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
		}

		activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
	}

/*	public void sendWeightTiming(){
		new Thread(){
			public void run(){
				try {

					Thread.sleep(1000*60*60);
					sendWeightTiming();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}*/

/*	public void doQueryData(){
		InfoDao infoDao = new InfoDao(activity);
		ArrayList<Weight> list = infoDao.query();
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				Weight weight = list.get(i);
				HttpGetData h = new HttpGetData();
				h.doSendWeightTiming(weight, infoDao,this);
			}
		}
	}*/
}
