package com.mzj.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hasee on 2017/12/1.
 */

public class PreferenceUtils {
 private static PreferenceUtils instance = null;
    //私有构造方法
    private PreferenceUtils(){
    }

    public static PreferenceUtils getInstance(){
        if(instance==null){
            instance = new PreferenceUtils();
        }
        return instance;
    }

    public  void putPreString(String key,String value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("mzj",Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        //提交编辑器内容
        editor.apply();
    }
    public String getPreString(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences("mzj", Context.MODE_PRIVATE);
       return sp.getString(key,null);
    }
}
