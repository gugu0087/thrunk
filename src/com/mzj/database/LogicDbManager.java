package com.mzj.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mzj.bean.Ad;
import com.mzj.scale_app.MainActivityHelper;
import com.mzj.util.Commons;
import com.mzj.util.DateUtils;
import com.mzj.util.MzjLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/12/2.
 */

public class LogicDbManager {

    public static LogicDbManager logicDbManager;

    private SQLiteDatabase database;
    private static final int DB_OLD_VERSION = 1;
    private static final int DB_NEW_VERSION = 1;
    private LogicDbManager( ) {
        LocalDbHelper dbHelper = LocalDbHelper.getInsatnce();
        database = dbHelper.getDatabase(Commons.adDbPath,Commons.AD_TABLE_NAME);
        dbHelper.onCreate(database);
        dbHelper.onUpgrade(database,DB_OLD_VERSION,DB_NEW_VERSION);
    }

    public static LogicDbManager getInstance() {
        if (logicDbManager == null) {
            logicDbManager = new LogicDbManager();
        }
        return logicDbManager;
    }

    public boolean isAdExitsById(String id){
        if(id==null){
            return false;
        }
        String sql = "select * from "+ Commons.AD_TABLE_NAME + " where id = ?";
        if(database==null){
            return false;
        }
        String[] strs= {String.valueOf(id)};
        Cursor cursor = database.rawQuery(sql, strs);
        while (cursor.moveToNext()){
            String idDb = cursor.getString(cursor.getColumnIndex("id"));
            if(idDb!=null){
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public void  addAdDataListToDb(final List<Ad> adList){

               if(adList==null||adList.size()==0){
                   return;
               }
               if(database==null){
                   return;
               }
               for(int i=0;i<adList.size();i++){
                   Ad ad = adList.get(i);
                   ContentValues values = new ContentValues();
                   values.put("id",ad.getId());
                   values.put("src",ad.getSrc());
                   values.put("fileType",ad.getFileType());
                   values.put("md5",ad.getMd5());
                   values.put("publish",ad.getPublish());
                   values.put("expire",ad.getExpire());
                   values.put("duration",ad.getDuration());
                   values.put("videoPlayNum",ad.getVideoPlayNum());
                   values.put("forUser",ad.getForUser());
                   values.put("status",ad.getStatus());
                   values.put("defaultAd",ad.getDefaultAd());
                   values.put("fileName",ad.getFileName());
                   values.put("taskId",ad.getTaskId());
                   database.replace(Commons.AD_TABLE_NAME,null,values);
                  /* if(isAdExitsById(ad.getId())){
                       String[] strs= {ad.getId()};
                       database.update(Commons.AD_TABLE_NAME,values,"id=?",strs);
                   }else {
                       database.insert(Commons.AD_TABLE_NAME,null,values);
                   }*/
               }
    }

    public void deleteDataByStatusAndExpire() {
        long curTime = DateUtils.getCurSystemTime();
        String sql = "delete from "+ Commons.AD_TABLE_NAME + " where expire < "+String.valueOf(curTime)+" or status = 2"; // status = 2 表示下架的
        database.execSQL(sql);
    }
    public void deleteDataById(String id){
        String sql = "delete from "+ Commons.AD_TABLE_NAME + " where id ="+id; // status = 2 表示下架的

        database.execSQL(sql);

    }
    public List<Ad> getAdDataList(){

        long curTime = DateUtils.getCurSystemTime();
        List<Ad> adList = new ArrayList<>();
       String sql = "select * from "+ Commons.AD_TABLE_NAME + " where expire >? and status = 1"; // status = 1 表示未下架的
        String[] strs= {String.valueOf(curTime)};
        Cursor cursor = database.rawQuery(sql, strs);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String src = cursor.getString(cursor.getColumnIndex("src"));
            String fileType = cursor.getString(cursor.getColumnIndex("fileType"));
            String md5 = cursor.getString(cursor.getColumnIndex("md5"));
            String publish = cursor.getString(cursor.getColumnIndex("publish"));
            String expire = cursor.getString(cursor.getColumnIndex("expire"));
            String duration = cursor.getString(cursor.getColumnIndex("duration"));
            String videoPlayNum = cursor.getString(cursor.getColumnIndex("videoPlayNum"));
            String forUser = cursor.getString(cursor.getColumnIndex("forUser"));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
            String taskId = cursor.getString(cursor.getColumnIndex("taskId"));

            Ad ad = new Ad();
            ad.setId(id);
            ad.setSrc(src);
            ad.setFileName(fileName);
            ad.setFileType(fileType);
            ad.setMd5(md5);
            ad.setPublish(publish);
            ad.setExpire(expire);
            ad.setDuration(duration);
            ad.setVideoPlayNum(videoPlayNum);
            ad.setForUser(forUser);
            ad.setStatus(status);
            ad.setTaskId(taskId);
            adList.add(ad);
        }
        cursor.close();
        return adList;
    }

    public void getReloadAdListDb(){
        List<Ad> adDataList = getAdDataList();
        if(Commons.imageVideoList==null||Commons.adForUserList==null||Commons.imageVideoList.size()==0){
            return;
        }
        List<String> idListDb = new ArrayList<>();

        for (int i = 0; i < adDataList.size(); i++) {
            idListDb.add(adDataList.get(i).getId());
        }

        List<String> idListComs = new ArrayList<>();

        for (int j = 0; j < Commons.imageVideoList.size(); j++) {
            idListComs.add(Commons.imageVideoList.get(j).getId());

        }
        List<String> idForUser = new ArrayList<>();
        for (int k = 0; k < Commons.adForUserList.size(); k++) {
            idForUser.add(Commons.adForUserList.get(k).getId());
        }
        boolean isContain =  idListDb.containsAll(idListComs)&&idListDb.containsAll(idForUser);
        boolean isSizeEqual = (idForUser.size()+idListComs.size())==idListDb.size();
        MzjLog.i("LogicDbManager","getReloadAdListDb+isContain="+isContain);
        MzjLog.i("LogicDbManager","getReloadAdListDb+isSizeEqual="+isSizeEqual);
        if(isContain&&isSizeEqual){
            return;
        }

        MainActivityHelper.initAdData();

    }

}

