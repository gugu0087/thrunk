package com.mzj.scale_app;
import android.content.Context;
import android.os.Handler;

import com.mzj.database.DbTaskManager;

/**
 * Created by hasee on 2017/12/4.
 */

public class ApplicationHolder {
    private static ApplicationHolder applicationHolder;
    private Context applicationContext;
    private static final Handler mainHandler = new Handler();
    private ApplicationHolder(){

    }
    public static ApplicationHolder getInstance(){
        if(applicationHolder==null){
            applicationHolder= new ApplicationHolder();
        }
        return applicationHolder;
    }
    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        if(applicationContext!=null){
            this.applicationContext = applicationContext;
        }
    }
    // 获取主线程
    public  void postMainRunnable(Runnable runnable) {
        mainHandler.post(runnable);
    }

   // 获取子线程
    public Handler getBackgroundHandler() {
        return DbTaskManager.getInstance().getReadHandler();
    }

}
