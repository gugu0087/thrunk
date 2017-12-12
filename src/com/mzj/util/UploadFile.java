package com.mzj.util;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件更新
 *
 * @author fyf000
 *
 */
public class UploadFile {

	public void updateAPk(final String url,final String taskType, final String taskId, final int time,final TaskListHelper taskListHelper) {

		Request request = HttpUtils.getAsynchronous(url);
		HttpUtils.client.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				int tempTime = time;
				tempTime--;
				MzjLog.i("updateAPk =", "服务端返回apk数据失败");
				if (tempTime >= 0)
					updateAPk(url,taskType,taskId, tempTime,taskListHelper);
				else{
					taskListHelper.sendTaskResult(false,taskType,taskId,null,Commons.SEND_TASK_RESULT);
				}
			}

			@Override
			public void onResponse(Response response) {
				boolean updateFlag = false;
				MzjLog.i("updateAPk", "开始下载apk");
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					is = response.body().byteStream();
					File downLoadFile = new File(Commons.apkPath);
					if (!downLoadFile.exists()) {
						downLoadFile.mkdir();
					} else {
						File files[] = downLoadFile.listFiles(); // 声明目录下所有的文件
						for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
							files[i].delete();
						}
					}

					int index = url.indexOf("?");
					String urlNew = url.substring(0, index);
					String fileName = urlNew.substring(urlNew.lastIndexOf("/"), urlNew.length());
					File file = new File(Commons.apkPath, fileName);
					fos = new FileOutputStream(file);
					long sum = 0;
					byte[] buf = new byte[2048];
					int len = 0;
					long total = response.body().contentLength();

					while (sum < total) {
						len = is.read(buf);
						fos.write(buf, 0, len);
						sum += len;
					}
					fos.flush();
					MzjLog.i("updateAPk", "下载apk完成1");

					String crc = url.substring(index + 1, url.length());
					String filePath = Commons.apkPath + fileName;
					String crcString = crc.substring(4);
					MzjLog.i("updateAPk","下发的md5="+crcString);
					boolean isCheckPass = FileMD5Compare.verifyInstallPackage(filePath, crcString);
					  if (isCheckPass) {
						// 后更新执行更新软件
						MzjLog.i("updateAPk", "更新apk");
						updateFlag = ControlAppUtil.install(filePath);
					} else {
						MzjLog.i("updateAPk", "下载文件校验失败");
						taskListHelper.sendTaskResult(false,taskType,taskId,null,Commons.SEND_TASK_RESULT);
						//return;
					}

				} catch (Exception e) {
					updateFlag  = false;
				} finally {
					MzjLog.i("updateAPk", "sendTaskResult");
					taskListHelper.sendTaskResult(updateFlag,taskType,taskId,null,Commons.SEND_TASK_RESULT);
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
	}
}