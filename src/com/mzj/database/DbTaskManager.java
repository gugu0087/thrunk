package com.mzj.database;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by hasee on 2017/12/12.
 */

public class DbTaskManager  {
    public static final String DB_WRITE_NAME = "db_write_thread";
    public static final String DB_READ_NAME = "db_read_thread";
    private Handler mWriteHandler;
    private Handler mReadHandler;
    private static volatile DbTaskManager instance;
    public static DbTaskManager getInstance() {
        if (instance == null) {
            synchronized (DbTaskManager.class) {
                instance = new DbTaskManager();
            }
        }
        return instance;
    }
    private DbTaskManager() {
        initWriteThread();
        initReadThread();
    }
    private void initWriteThread() {
        HandlerThread writeHandlerThread = new HandlerThread(DB_WRITE_NAME);
        writeHandlerThread.start();
        mWriteHandler = new Handler(writeHandlerThread.getLooper());
    }

    private void initReadThread() {
        HandlerThread readHandlerThread = new HandlerThread(DB_READ_NAME);
        readHandlerThread.start();
        mReadHandler = new Handler(readHandlerThread.getLooper());
    }

    public void executeWriteDbTask(Runnable runnable) {
        if(runnable == null){
            return;
        }
        mWriteHandler.post(runnable);
    }

    public void executeWriteDbTaskDelay(Runnable runnable, long delayMillis) {
        if(runnable == null){
            return;
        }
        mWriteHandler.postDelayed(runnable, delayMillis);
    }

    public void executeReadDbTask(Runnable runnable) {
        if(runnable == null){
            return;
        }
        mReadHandler.post(runnable);
    }

    public void executeReadDbTaskDelay(Runnable runnable, long delayMillis) {
        if(runnable == null){
            return;
        }
        mReadHandler.postDelayed(runnable, delayMillis);
    }

    public Handler getWriteHandler() {
        return mWriteHandler;
    }

    public Handler getReadHandler() {
        return mReadHandler;
    }
}
