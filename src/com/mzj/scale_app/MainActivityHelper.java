package com.mzj.scale_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.mzj.bean.Ad;
import com.mzj.crashHandler.CrashActivityList;
import com.mzj.crashHandler.CrashApplication;
import com.mzj.database.LogicDbManager;
import com.mzj.logic.LogicManager;
import com.mzj.util.ApkInfo;
import com.mzj.util.AssetsCopyer;
import com.mzj.util.Commons;
import com.mzj.util.DateUtils;
import com.mzj.util.FileUtils;
import com.mzj.util.MzjLog;
import com.mzj.util.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * 开机页面辅助类，负责开启启动数据处理
 * @author Administrator
 *
 */
public class MainActivityHelper {


	private Activity activity;
	public MainActivityHelper(Activity activity){
		this.activity = activity;
	}

	public void init(){
		MzjLog.i("copyAssetsFile", "copyAssetsFile");
		copyAssetsFile();//拷贝文件到板子上
		MzjLog.i("copyOldRQCode", "copyOldRQCode");
		//copyOldRQCode();//拷贝旧版本的二维码到新项目的image目录
		MzjLog.i("initLog", "initLog");
		initLog();//初始化日志
		MzjLog.i("addAcitivity", "addAcitivity");
		CrashActivityList.addAcitivity(activity);//将当前activity加入到异常处理程序中，出现bug,关闭程序
		MzjLog.i("getDevicesID", "getDevicesID");
		getDevicesID();//获取设备id
		MzjLog.i("new ApkInfo", "new ApkInfo");
		new ApkInfo();//初始化apk版本信息
		MzjLog.i("initAdData", "initAdData");
		// 把之前配置文件里的广告读进数据库
		//insertOldAdDataToDb();
		initAdData();//初始化广告
		MzjLog.i("initAdData", "initAdData22");

	}

    private void  insertOldAdDataToDb(){

		LogicManager.getInstance().getBackgroundHandler().post(new Runnable() {
			@Override
			public void run() {
				JSONArray adDataJsonArray = null;
				try {
					PropertiesUtil propertiesUtil = new PropertiesUtil();
					Properties properties = propertiesUtil.loadConfig(Commons.configPath);
					if(properties==null){
						return;
					}

					String adData = (String) properties.get("adData");
					if(adData==null){
						LogicManager.getInstance().deleteAdOrImg();
						return;
					}
					adDataJsonArray = new JSONArray(adData);
					List<Ad> adList = new ArrayList<>();
					if(adDataJsonArray.length()==0){
						return;
					}
					for(int i =0;i<adDataJsonArray.length();i++){
						JSONObject taskObj = adDataJsonArray.getJSONObject(i);
						String taskDetail = taskObj.getString("taskDetail");
						String taskId = taskObj.getString("taskId");
						JSONArray detailArray = new JSONArray(taskDetail);
						if(detailArray.length()==0){
							return;
						}
						for(int j=0;j<detailArray.length();j++){
							String adString = detailArray.getString(i);

							Gson gson = new Gson();
							Ad ad = gson.fromJson(adString,Ad.class);
							ad.setTaskId(taskId);
							String src = ad.getSrc();
							String fileName = src.substring(src.lastIndexOf("/")+1, src.length());
							ad.setFileName(taskId+"_"+fileName);
							adList.add(ad);
						}
					}
					LogicDbManager.getInstance().addAdDataListToDb(adList);

					properties.put("adData","");
					propertiesUtil.saveConfig(Commons.configPath,
							properties);

					MzjLog.i("MainActivityHelper","insertOldAdDataToDb-adList="+adList);
				} catch (JSONException e) {
					MzjLog.i("MainActivityHelper","insertOldAdDataToDb-e="+e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 拷贝assets文件夹到磁盘上
	 */
	public void copyAssetsFile(){
		File file = new File(Commons.applicationPath);
		//如果不存在就拷贝整个文件夹
		if(!file.exists()){
			AssetsCopyer.releaseAssets(CrashApplication.getAppContext(), Commons.MZJ,android.os.Environment.getExternalStorageDirectory().getPath());
		}else{
			try {
				//如果存在就拷贝两个音乐文件
				File musicFile = new File(Commons.musicPath);
				// 如果存在目录就删除下面的文件，否则创建
				if (musicFile.exists()) {
					File files[] = musicFile.listFiles(); // 声明目录下所有的文件 files[];
					if(files!=null){
						for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
							files[i].delete();
						}
					}
				}
				AssetsCopyer.releaseAssets(CrashApplication.getAppContext(), Commons.MZJ+"/music",android.os.Environment.getExternalStorageDirectory().getPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 拷贝旧的二维码
	 */
	public void copyOldRQCode(){
		File file = new File(Commons.applicationOldPath);
		if(file.exists()){
			FileUtils fileUtils = new FileUtils();
			boolean isCopySucess = fileUtils.copySingleFile(Commons.applicationOldPath+"image/image_rqCode.png", Commons.applicationPath+"image/image_rqCode.png");
			MzjLog.i("copyOldRQCode","isCopySucess = "+isCopySucess);
			if(isCopySucess){
				fileUtils.deleteFile(file);
			}

		}
	}


	/**
	 * 初始化日志
	 */
	public void initLog() {
		String LOG_PATH = Commons.infoPath+ new SimpleDateFormat(MzjLog.DATE_FORMAT).format(new Date());
		MzjLog.init(LOG_PATH);
		MzjLog.i("日志路径:",LOG_PATH);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		MzjLog.i(MainActivity.class.getName(), "初始化应用程序");
	}


	/**
	 * 获取设备id
	 */
	public void getDevicesID(){
		TelephonyManager tm = (TelephonyManager) CrashApplication.getAppContext().getSystemService(Activity.TELEPHONY_SERVICE);
		Commons.deviceId = tm.getDeviceId();//设备id
		if(Commons.deviceId==null){
			Commons.deviceId = Secure.getString(CrashApplication.getAppContext().getContentResolver(), Secure.ANDROID_ID);
		}
	}


	/**
	 * 初始化图片和视频数据
	 */
	public static void initAdData() {
		List<Ad> adDataList = LogicDbManager.getInstance().getAdDataList();

		PropertiesUtil propertiesUtil = new PropertiesUtil();
		Properties properties = propertiesUtil.loadConfig(Commons.configPath);
        List<Ad> imageVideoList = new ArrayList<Ad>();
		List<Ad> adForUserList = new ArrayList<Ad>();
		if(adDataList!=null&&adDataList.size()>0){
			//加载非默认广告  人没站在秤上
			addAdDataToList(adDataList,imageVideoList,"0");
			//加载非默认广告  人站在秤上
			addAdDataToList(adDataList,adForUserList,"1");
		}
		if(imageVideoList.size()==0){
			//加载默认广告  人没站在秤上
            if(properties!=null){
				String defaultData = (String) properties.get("defaultAdData");
				addDefaultAdToList(defaultData,imageVideoList,"0",true);
			}
		}

		if(adForUserList.size() == 0){
			//加载默认广告 人站在秤上
			if(properties!=null){
				String defaultDataForUser = (String) properties.get("defaultAdData");
				addDefaultAdToList(defaultDataForUser,adForUserList,"1",true);
			}
		}

		Commons.imageVideoList = imageVideoList;
		Commons.adForUserList = adForUserList;
		MzjLog.i("MainActivityHelper", "initAdData-imageVideoList="+imageVideoList);
		MzjLog.i("MainActivityHelper", "initAdData-adForUserList="+adForUserList);
	}

   public static void  addDefaultAdToList(String data,List<Ad> imageVideoList,String forUserType,boolean isDefault){
	      if(data==null||data.isEmpty()){
			  return;
		  }
	      JSONArray adDataJsonArray = null;
	   try {
		   adDataJsonArray = new JSONArray(data);
		   for(int i=0;i<adDataJsonArray.length();i++) {
			   String adString = adDataJsonArray.getString(i);
			   Gson gson = new Gson();
			   Ad ad = gson.fromJson(adString, Ad.class);
			   //先判断是否下架，下架的不加载
			   String status = ad.getStatus();
			   if(status==null || "0".equals(status)){
				   return;
			   }
			   //判断过期
			   if((!isDefault)&& DateUtils.judgeCurrTime(Long.valueOf(ad.getExpire())))
			   {
				   return;
			   }
			   //默认图片或者视频的路径
			   if(ad.getDefaultAd()!=null && ad.getDefaultAd().equals("1")){
				   ad.setFilePath(Commons.adInitPath+ad.getSrc());
			   }

			   //判断是否是人站，还是平时播放的
			   if(ad.getForUser().equals(forUserType)){
				   String fileType = ad.getFileType();
				   //如果是视频
				   if(fileType.equals(Ad.VIDEOTYPE)){
					   String playNum = ad.getVideoPlayNum();
					   if(playNum==null || "".equals(playNum)){
						   playNum = "1";
					   }
					   int videoPlayNum = new Integer(playNum);
					   for(int j=0;j<videoPlayNum;j++){
						   Ad adClone = (Ad)ad.clone();
						   imageVideoList.add(adClone);
					   }
				   }else{
					   imageVideoList.add(ad);
				   }
			   }

		   }

	   } catch (JSONException e) {
		   e.printStackTrace();
	   }
   }
	/**
	 * 把数据添加到list里面
	 * @param
	 * @param imageVideoList
	 * @param
	 */
	@SuppressLint("UseValueOf")
	public static void addAdDataToList(List<Ad> dbAdList,List<Ad> imageVideoList,String forUserType){
		if (dbAdList == null || dbAdList.size() == 0) {
			return;
		}
		for (int i = 0; i < dbAdList.size(); i++) {
			Ad ad = dbAdList.get(i);
			//默认图片或者视频的路径
			if(ad.getDefaultAd()!=null && ad.getDefaultAd().equals("1")){
				ad.setFilePath(Commons.adInitPath+ad.getFileName());
			}else{
				ad.setFilePath(Commons.adDownloadPath+ad.getFileName());
			}

			//判断是否是人站，还是平时播放的
			if(ad.getForUser().equals(forUserType)){
				String fileType = ad.getFileType();
				//如果是视频
				if(fileType.equals(Ad.VIDEOTYPE)){
					String playNum = ad.getVideoPlayNum();
					if(playNum==null || "".equals(playNum)){
						playNum = "1";
					}
					int videoPlayNum = new Integer(playNum);
					for(int j=0;j<videoPlayNum;j++){
						Ad adClone = (Ad)ad.clone();
						imageVideoList.add(adClone);
					}
				}else{
					imageVideoList.add(ad);
				}
			}
		}





	}/**

/*	 * 把数据添加到list里面
	 * @param adData
	 * @param imageVideoList
	 * @param forUserType
	 *//*
	@SuppressLint("UseValueOf")
	public static void addAdDataToList(String adData, List<Ad> imageVideoList, String forUserType, String taskId,boolean isdefault){
		if(adData == null || "".equals(adData))return;

		JSONArray adDataJsonArray = null;
		try {
			adDataJsonArray = new JSONArray(adData);
			for(int i=0;i<adDataJsonArray.length();i++){
				String adString = adDataJsonArray.getString(i);
				Gson gson = new Gson();
				Ad ad = gson.fromJson(adString,Ad.class);
				//先判断是否下架，下架的不加载
				String status = ad.getStatus();
				if(status==null || "0".equals(status)){
					return;
				}
				//判断过期
				if((!isdefault)&& DateUtils.judgeCurrTime(Long.valueOf(ad.getExpire())))
				{
					return;
				}
				//默认图片或者视频的路径
				if(ad.getDefaultAd()!=null && ad.getDefaultAd().equals("1")){
					ad.setFileName(Commons.adInitPath+ad.getSrc());
				}else{
					String src = ad.getSrc();
					String fileName = src.substring(src.lastIndexOf("/")+1, src.length());
					ad.setFileName(Commons.adDownloadPath+taskId+"_"+fileName);
				}

				//判断是否是人站，还是平时播放的
				if(ad.getForUser().equals(forUserType)){
					String fileType = ad.getFileType();
					//如果是视频
					if(fileType.equals(Ad.VIDEOTYPE)){
						String playNum = ad.getVideoPlayNum();
						if(playNum==null || "".equals(playNum)){
							playNum = "1";
						}
						int videoPlayNum = new Integer(playNum);
						for(int j=0;j<videoPlayNum;j++){
							Ad adClone = (Ad)ad.clone();
							imageVideoList.add(adClone);
						}
					}else{
						imageVideoList.add(ad);
					}
				}


			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}*/
}
