package com.mzj.logic;

import android.content.Context;
import android.os.Handler;

import com.mzj.database.LogicDbManager;
import com.mzj.scale_app.ApplicationHolder;
import com.mzj.util.Commons;
import com.mzj.util.FileUtils;

/**
 * Created by hasee on 2017/12/6.
 */

public class LogicManager implements LogicInterface {
    private static LogicManager instance = null;
    private LogicManager() {

    }

    public static LogicManager getInstance() {
        if (instance == null) {
            return new LogicManager();
        }
        return instance;
    }


    /**
     * 删除本地数据库的广告数据和downLoad目录下的广告图片
     */
    @Override
    public void deleteAdOrImg(){
        LogicDbManager.getInstance().deleteDataByStatusAndExpire();
        FileUtils.deleteAdOrVideo();
    }

    /**
     * 通过id删除下架的广告
     * @param id
     */
    @Override
   public void deleteAdById(String id){
       if(id==null){
           return;
       }
       LogicDbManager.getInstance().deleteDataById(id);
   }

    /**
     * 获取主线程
     * @param runnable
     */
    @Override
    public void getMainThread(Runnable runnable) {
        ApplicationHolder.getInstance().postMainRunnable(runnable);
    }

    /**
     * 获取后台线程
     * @return
     */
    @Override
    public Handler getBackgroundHandler() {
        return ApplicationHolder.getInstance().getBackgroundHandler();
    }

    @Override
    public Context getApplicationContext(){
       return ApplicationHolder.getInstance().getApplicationContext();
    }

    /**
     * 重新刷新当前播放广告列表
     */
    @Override
    public void getReloadAdList() {
        LogicDbManager.getInstance().getReloadAdListDb();
    }

}
