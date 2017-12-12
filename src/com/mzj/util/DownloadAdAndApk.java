package com.mzj.util;

import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.mzj.bean.Ad;
import com.mzj.database.LogicDbManager;
import com.mzj.logic.LogicManager;
import com.mzj.scale_app.MainActivityHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 下载广告和apk
 * @author Administrator
 *
 */
public class DownloadAdAndApk {

	private String taskId;
	private String taskDetailStr;

	public DownloadAdAndApk(String taskId,String taskDetailStr){
		this.taskId = taskId;
		this.taskDetailStr = taskDetailStr;
	}
	/**
	 * 清除文件夹下面的所有文件
	 * @param fileName
	 */
	public void clearfolders(String fileName){
		try {
			// 新建文件夹
			File file = new File(fileName);
			// 如果存在目录就删除下面的文件，否则创建
			if (file.exists()) {
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					files[i].delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 下载广告
	 */
	public void downloadAd(){
		//
		int num = 0;
		JSONArray taskDetail;
		try {
			taskDetail = new JSONArray(taskDetailStr);
			File file = new File(Commons.adDownloadPath);
			if(!file.exists()){
				file.mkdir();
			}
			if (taskDetail.length() > num) {
				doUpdateAd(num, Commons.DOWNLOAD_RETRY_NUMBNER,taskDetail);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    final List<Ad> adList = new ArrayList<>();
	public void doUpdateAd(final int num, final int time,final JSONArray taskDetail) {

		JSONObject jsonObject;
		try {
			jsonObject = taskDetail.getJSONObject(num);
		     if(jsonObject==null){
				 return;
			 }
			Log.d("xyc", "doUpdateAd: jsonObject.toString()="+jsonObject.toString());
			Gson gson = new Gson();
			final Ad ad = gson.fromJson(jsonObject.toString(), Ad.class);
			String status = ad.getStatus();
			String id = ad.getId();
			int tepNum = num;
			if(status.equals("2")){
				LogicManager.getInstance().deleteAdById(id);
				FileUtils.deleteAdOrVideo();
				tepNum ++;
				if(tepNum!=taskDetail.length()){
					doUpdateAd(tepNum, time, taskDetail);
					return;
				}else {
                    MainActivityHelper.initAdData();
					TaskListHelper t = new TaskListHelper();
					t.sendTaskResult(true,TaskListHelper.ad,taskId,null,Commons.SEND_TASK_RESULT);
					return;
				}
			}

			String strs[]=ad.getSrc().split("[?]");
			if(strs.length>0){
				ad.setFileName(taskId+"_"+strs[0].substring(
						ad.getSrc().lastIndexOf("/")+1, strs[0].length()));
			}

			MzjLog.i("DownloadAdAndApk", "onFailure-doUpdateAd-jsonObject="+jsonObject.toString());
			Request request = HttpUtils.getAsynchronous(ad.getSrc());
			HttpUtils.client.newCall(request).enqueue(new Callback() {

				@Override
				public void onFailure(Request arg0, IOException arg1) {
					// 失败重新下载，最多下载10次
					Log.d("xyc", "onFailure: arg1="+arg1.getMessage());
					int tempTime = time;
					tempTime--;
					if (tempTime > 0){
						doUpdateAd(num, tempTime,taskDetail);
					}else{
						TaskListHelper t = new TaskListHelper();
						t.sendTaskResult(false, TaskListHelper.ad,taskId,null,Commons.SEND_TASK_RESULT);
					}

					MzjLog.i("DownloadAdAndApk", "onFailure-doUpdateAd-arg1="+arg1.getMessage());
				}

				@Override
				public void onResponse(Response response) throws IOException {

					InputStream is = response.body().byteStream();
					/*String fileName = ad.getSrc().substring(
							ad.getSrc().lastIndexOf("/")+1, ad.getSrc().length());*/

					File file = new File(Commons.adDownloadPath,ad.getFileName());

					FileOutputStream fos = new FileOutputStream(file);
					try {
						long sum = 0;
						byte[] buf = new byte[2048*2];
						int len = 0;
						long total = response.body().contentLength();

						while (sum < total) {
							len = is.read(buf);
							fos.write(buf, 0, len);
							sum += len;
							fos.flush();
						}

						String filePath = Commons.adDownloadPath +ad.getFileName();

						boolean isCheckPass = FileMD5Compare.verifyInstallPackage(filePath, ad.getMd5());
						if (isCheckPass) {
							ad.setTaskId(taskId);
							adList.add(ad);
							int tempNum = num;
							// 加载下一个数据
							tempNum++;
							MzjLog.i("下载广告", num + "");

							// 图片和视频全部下载完，然后记录数据，然后下载apk版本
							if (tempNum == taskDetail.length()) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										LogicDbManager.getInstance().addAdDataListToDb(adList);
										MzjLog.i("DownloadAdAndApk", "onResponse-doUpdateAd-adList="+adList);
										TaskListHelper t = new TaskListHelper();
										t.sendTaskResult(true, TaskListHelper.ad, taskId, null,Commons.SEND_TASK_RESULT);
										// 重新加载广告数据
										MainActivityHelper.initAdData();
										LogicManager.getInstance().deleteAdOrImg();
									}
								}).start();
							} else {
								MzjLog.i("DownloadAdAndApk", "onResponse-doUpdateAd-tempNum="+tempNum);
								doUpdateAd(tempNum, time, taskDetail);
							}
						}else{
							MzjLog.i("下载广告校验失败", "下载广告校验失败");
							TaskListHelper t = new TaskListHelper();
							t.sendTaskResult(false,TaskListHelper.ad,taskId,null,Commons.SEND_TASK_RESULT);
							return;
						}
					} catch (Exception e) {
						doUpdateAd(num, 10,taskDetail);
						Log.d("xyc", "onResponse: e"+e.getMessage());
						e.printStackTrace();
					} finally {
						try {
							if (is != null)
								is.close();
						} catch (IOException e) {
						}
						try {
							if (fos != null)
								fos.close();
						} catch (IOException e) {
						}
					}
				}

			});
		} catch (Exception e) {
			TaskListHelper t = new TaskListHelper();
			t.sendTaskResult(false, TaskListHelper.ad, taskId, null,Commons.SEND_TASK_RESULT);
			e.printStackTrace();
		}

	}
}
