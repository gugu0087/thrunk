package com.mzj.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.mzj.util.Commons;
import java.io.File;

/**
 * Created by hasee on 2017/12/2.
 */

public class LocalDbHelper  {
   private Context mContext;
    private static LocalDbHelper dbHelper = null;


    public static LocalDbHelper getInsatnce(){
        if(dbHelper==null){
            dbHelper = new LocalDbHelper();
        }
        return dbHelper;
    }

    // 初始化数据库
    public  SQLiteDatabase getDatabase(String dataBasePath, String tableName) {
        try {
            String databaseFilename = dataBasePath + "/" + tableName;
            File dir = new File(dataBasePath);
            if (!dir.exists())
                dir.mkdir();

            return SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     for(int i = oldVersion;i<newVersion;i++){
         switch (i){
             case 1:
                 updateVersion1(db);
                 break;
             default:
                 break;
         }
     }
    }

    private void updateVersion1(SQLiteDatabase db) {

    }

    private void  createTable(SQLiteDatabase db){
        if(db==null){
            return;
        }
        // 创建广告数据库表
        String sql ="create table if not exists "+ Commons.AD_TABLE_NAME +"( id varchar(20) primary key,src varchar(50),fileType varchar(20),md5 varchar(20),publish varchar(20),expire String(20),duration varchar(20),videoPlayNum varchar(20),forUser varchar(20),status varchar(20),defaultAd varchar(20),fileName varchar(20),taskId varchar(20))";
        db.execSQL(sql);
    }
}
