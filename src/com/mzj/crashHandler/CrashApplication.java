package com.mzj.crashHandler;


import android.app.Application;
import android.content.Context;
import android.os.Vibrator;

import com.mzj.scale_app.ApplicationHolder;
import com.mzj.service.LocationService;


public class CrashApplication extends Application {  
	
	public static LocationService locationService;
    public Vibrator mVibrator;
    private static Context context; 
    @Override  
    public void onCreate() {  
        super.onCreate();  
        CrashApplication.context = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());  
        
          /***
         * 初始化定位sdk，建议在Application中创        
         * */
        locationService = new LocationService(getApplicationContext());
//        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
//        SDKInitializer.initialize(getApplicationContext());
         ApplicationHolder.getInstance().setApplicationContext(this);
    }  
    public static Context getAppContext() { 
        return CrashApplication.context;
    } 
} 