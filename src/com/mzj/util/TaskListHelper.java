package com.mzj.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mzj.scale_app.MainActivityHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class TaskListHelper {

	//重启设备
	public static final String reBoot = "100";
	//定时开关机
	public static final String timeSwitch = "101";
	//心跳时间配置任务
	public static final String heartBeatTime = "102";
	//设备运行环境
	public static final String operatingEnvironment = "103";
	//日志上传
	public static final String logUpload = "104";
	//音量调节
	public static final String volume = "105";
	//屏幕亮度调节
	public static final String screenBrightness = "106";
	//信号信息显示或者隐藏
	public static final String signal = "107";
	//更新apk
	public static final String updateAPK = "200";
	//订阅号二维码更新任务
	public static final String QRCode = "201";
	//广告发布任务
	public static final String ad = "202";
	//界面转换
	public static final String pagetype = "110";

	public static String timeSwitchId = "";

	private Activity activity;
	private Handler handler;

	public TaskListHelper(){
	}

	public TaskListHelper(Activity activity,Handler handler){
		this.activity = activity;
		this.handler = handler;
	}

	public static String fistLineStr;

	/**
	 * 执行任务
	 * @param taskId 任务id
	 * @param
	 * @param
	 */
	public boolean executeTheTask(String taskId,String taskType,String taskDetailStr){

		//格式转换
		JSONObject taskDetail = null;
		if(!ad.equals(taskType)&&!"reBoot".equals(taskType)&&!"logUpload".equals(taskType)){
			try {
				taskDetail = new JSONObject(taskDetailStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		//发送结果失败任务列表存在，则不执行，直接发送成功结果
		PropertiesUtil pu = new PropertiesUtil();
		Properties p = pu.loadConfig(Commons.failTaskListFile);
		String key = "taskId"+taskId;
		String failTaskId = p.getProperty(key);
		if(failTaskId!=null && failTaskId.equals(taskId)){
			p.remove(key);
			pu.saveConfigNoBack(Commons.failTaskListFile, p);
			return true;
		}



		boolean taskResult = true;
		try{
			ControlAppUtil controlAppUtil = new ControlAppUtil(activity);
			HttpGetData httpGetData = new HttpGetData(handler);
			switch(taskType){
				case timeSwitch:
					timeSwitchId = taskId;
					//controlAppUtil.timeSwitch(taskType, taskDetail);
					break;
				case reBoot:

					sendTaskResult(true,taskType, taskId,null,Commons.SEND_TASK_RESULT);
					break;
				case heartBeatTime:
					String interval = taskDetail.getString("interval");
					String delay = taskDetail.getString("delay");
					httpGetData.reSetHeartbeatTime(new Integer(delay),new Integer(interval));
					break;
				case operatingEnvironment:
					break;
				case logUpload://日志上传
					HttpUtils httpUtils = new HttpUtils();
					//压缩文件
					ZipUtil.zipFolder(Commons.logPath, Commons.logPathZip);
					//上传文件
					taskResult = httpUtils.upLoadFile(Commons.uploadLog, Commons.logPathZip);
					break;
				case volume://音量调节
					String sound = taskDetail.getString("sound");
					MusicThread.volume = new Float(sound);
					break;
				case screenBrightness://屏幕亮度调节
					Message msg = new Message();
					msg.what = 20;
					List<Object> list = new ArrayList<Object>();
					String screen = taskDetail.getString("screen");
					list.add(new Float(screen));
					list.add(taskId);
					msg.obj = list;
					handler.sendMessage(msg);
					break;
				case signal://信号信息显示或者隐藏
					String hide = taskDetail.getString("hide");
					sendMessage(handler,25,hide);
					break;
				case updateAPK://更新apk
					updateApk(taskId,taskType,taskDetail);
					break;
				case QRCode://订阅号二维码更新任务
					httpGetData.getQRCode(taskId,taskType,taskDetail);
                    break;
				case ad://广告发布任务
					handelAdData(taskId,taskType,taskDetailStr);
					break;
				case pagetype://信号信息显示或者隐藏
					sendMessage(handler,26,taskDetail);
					break;
			}
		}catch(Exception e){
			taskResult = false;
		}
		return taskResult;
	}

	public void sendMessage(Handler handler,int what,Object obj) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		handler.sendMessage(msg);
	}

	//直接更新apk
	public void updateApk(String taskId,String taskType,JSONObject taskDetail){
		try{
			if (taskDetail != null) {
				// 如果版本号不相等，那么下载apk包
				String apkVersion = taskDetail.getString("version");
				MzjLog.i("updateApk=","apkVersion"+apkVersion);
				String apkInfoVersionCode = ApkInfo.getApkInfoVersionCode();
				MzjLog.i("updateApk=","apkInfoVersionCode"+apkInfoVersionCode);
				if (!ApkInfo.getApkInfoVersionCode().equals(apkVersion)) {
					String scalesapkUrl = taskDetail.getString("apk");
					// 下载apk包
					UploadFile uploadFile = new UploadFile();
					uploadFile.updateAPk(scalesapkUrl,taskType,taskId,Commons.DOWNLOAD_RETRY_NUMBNER,this);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	//发送执行任务结果
	public  void sendTaskResult(final boolean taskResult,final String taskType,final String taskId,final JSONObject taskDetail,final int num){

		//组装参数
		JSONObject params = new JSONObject();
		try {
			MzjLog.i("发送任务","任务类型"+taskType);
			params.put("taskId",taskId);//任务id
			params.put("machineId", Commons.deviceId);//设备id
			params.put("taskType",taskType);//任务类型
			String flag = taskResult==true?"1":"0";
			params.put("result",flag);//执行任务结果
			params.put("reqTime",new Date().getTime());//发送时间
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String url = Commons.executeTaskResultUrl;

		Request request = HttpUtils.postAsynchronous(url, params);
		HttpUtils.client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				MzjLog.i("发送任务失败", "任务id"+taskId+"  "+"任务类型"+taskType);
                //发送失败，记录文本
				if(num<=1) {
					PropertiesUtil pu = new PropertiesUtil();
					Properties p = pu.loadConfig(Commons.failTaskListFile);
					p.put("taskId" + taskId, taskId);
					pu.saveConfigNoBack(Commons.failTaskListFile, p);
				}else{
					sendTaskResult( taskResult,taskType,taskId, taskDetail, num-1);
					}
			}
			@Override
			public void onResponse(Response response) throws IOException {
				if(reBoot.equals(taskType)){
					ControlAppUtil controlAppUtil = new ControlAppUtil(activity);
					try {
						controlAppUtil.controlApp(Commons.controlType_nowReboot);
					} catch (Exception e) {
						MzjLog.i("重啟失敗 任務id", taskId);
                    }
				}


				//休眠10秒
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				MzjLog.i("发送任务成功", "任务id"+taskId+"  "+"任务类型"+taskType);
			}
		});
	}


	/**
	 * 定时开关机发送结果
	 * @param taskResult
	 * @param type
	 */
	public void sendTaskResultForTimeSwitch(final boolean taskResult,final String type){
		sendTaskResult(taskResult,type,timeSwitchId,null,Commons.SEND_TASK_RESULT);
		timeSwitchId = "";
	}

	/**
	 * 处理广告任务
	 * if 如果配置文件没有广告数据，那么直接下载广告
	 * else 先删除临时文件，然后，匹配任务id，匹配中，直接覆盖数据，匹配不中去下载广告，然后保存数据
	 *
	 * @param taskId
	 * @param taskType
	 * @param taskDetailStr
	 */
	@SuppressLint("UseValueOf")
	public void handelAdData(String taskId, String taskType, String taskDetailStr) {
		//PropertiesUtil propertiesUtil = new PropertiesUtil();
		//Properties properties = propertiesUtil.loadConfig(Commons.configPath);
		//String adData = properties.getProperty("adData");
		DownloadAdAndApk downloadAd = new DownloadAdAndApk(taskId, taskDetailStr);
		downloadAd.downloadAd();
/*
		if (adData == null || "".equals(adData)) {
			downloadAd.clearfolders(Commons.adDownloadPath);
			downloadAd.downloadAd();
		} else {
			try {
				// 任务id
				List<String> taskIdList = new ArrayList<String>();
				// 任务文件
				List<String> taskFileList = new ArrayList<String>();
				JSONArray array = new JSONArray(adData);

				for (int i = 0; i < array.length(); i++) {
					JSONObject taskObj = array.getJSONObject(i);
					String tId = taskObj.getString("taskId");
					taskIdList.add(tId);
					String taskDetail = taskObj.getString("taskDetail");
					JSONArray arrayDetail = new JSONArray(taskDetail);
					for (int j = 0; j < arrayDetail.length(); j++) {
						JSONObject object = arrayDetail.getJSONObject(j);
						String src = object.getString("src");
						String fileName = src.substring(src.lastIndexOf("/") + 1, src.length());
						taskFileList.add(tId + "_" + fileName);
					}
				}

				// 遍历文件，删除临时文件

				File file = new File(Commons.adDownloadPath);
				// 如果存在目录就删除下面的文件，否则创建
				if (file.exists()) {
					File files[] = file.listFiles(); // 声明目录下所有的文件 files[];

					for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
						String fileName = files[i].getName();
						fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
						if (!taskFileList.contains(fileName)) {
							files[i].delete();
						}
					}
				}

				// 任务id不相等才去下载图片和视频
				if (!taskIdList.contains(taskId)) {
					downloadAd.downloadAd();
				} else {
					// 如果任务id和配置文件里面的任何一条相等，那么覆盖数据
					for (int i = 0; i < array.length(); i++) {
						JSONObject taskObj = array.getJSONObject(i);
						String tId = taskObj.getString("taskId");
						if (tId.equals(taskId)) {
							taskObj.put("taskDetail", taskDetailStr);
							properties.put("adData", array.toString());
							propertiesUtil.saveConfig(Commons.configPath,
									properties);
						}
					}
					TaskListHelper t = new TaskListHelper();
					t.sendTaskResult(true, TaskListHelper.ad,taskId,null);
					// 重新加载广告数据
					MainActivityHelper.initAdData();
				}
			} catch (Exception e) {
				MzjLog.i("handelAdData", "处理广告数据异常");
				e.printStackTrace();
			}
		}*/
	}

	/**
	 * 删除文件第一行
	 * @param file
	 * @param
	 */
	public void removeFirstLine(String file){
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = null;
			int i=0;
			StringBuffer sb = new StringBuffer();
			while((line=br.readLine())!=null){
				i= i+1;
				if(i!=1){
					sb.append(line);
				}
			}

			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(sb.toString());// 往已有的文件上添加字符串
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{

				if(br!=null)br.close();
				if(fr!=null)fr.close();
				if(bw!=null)bw.close();
				if(fw!=null)fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
