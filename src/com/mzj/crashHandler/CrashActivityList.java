package com.mzj.crashHandler;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

public class CrashActivityList {

    private static List<Activity> activityList = new LinkedList<Activity>();

    public static void addAcitivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActitvity(Activity activity){
        if(activityList.contains(activity)){
            activityList.remove(activity);
        }
    }
    // 遍历所有Activity并finish
    public static void exit() {
        for (Activity activity : activityList) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
