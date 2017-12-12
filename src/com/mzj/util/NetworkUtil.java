package com.mzj.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

/**
 * 网络工具类
 * @author fyf
 *
 */
public class NetworkUtil
{

	/**
	 * dp2px
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, float dp)
	{
		int px = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
						.getDisplayMetrics()));
		return px;
	}

	// 判断是否有网络
	public static boolean isNetworkConnected(Context context) {
		boolean isConnected=false;
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			/*//网络
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null&&mNetworkInfo.isAvailable()) {
				isConnected=true;
			}*/
			//wifi
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        if (mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable()) {  
	        	isConnected=true;
	        } 
	        //3G
	        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	        if (mMobileNetworkInfo != null && mMobileNetworkInfo.isAvailable()) {  
	        	isConnected=true;  
	        }  
		}
		return isConnected;
	}
}
