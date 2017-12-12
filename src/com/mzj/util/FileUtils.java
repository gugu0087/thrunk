package com.mzj.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;

import com.mzj.bean.Ad;
import com.mzj.database.LogicDbManager;
import com.mzj.delegate.ISaveQrCodeDelegate;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class FileUtils {

	private static final String IMG_PATH = Commons.applicationPath + "/img";
	private  static final String DEFAULT_IMAGE_NAME = "image_rqCode.png";
	public String getImgParentPath() {
		return IMG_PATH;
	}



	/**
	 * 复制单个文件
	 *
	 * @param oldPathFile
	 *            准备复制的文件源
	 * @param newPathFile
	 *            拷贝到新绝对路径带文件名(注：目录路径�?��文件�?
	 * @return
	 */
	public boolean copySingleFile(String oldPathFile, String newPathFile) {
		boolean result = false;
		try {
			// int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPathFile);
			if (oldfile.exists()) { // 文件存在�?
				InputStream inStream = new FileInputStream(oldPathFile); // 读入原文�?
				@SuppressWarnings("resource")
				FileOutputStream fs = new FileOutputStream(newPathFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					// bytesum += byteread; // 字节�?文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				result = true;
			}
		} catch (Exception e) {
			MzjLog.i("copySingleFile", "copySingleFile = "+e.getMessage().toString());
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 复制整个文件夹的内容
	 *
	 * @param oldPath
	 *            准备拷贝的目
	 * @param newPath
	 *            指定绝对路径的新目录
	 * @return
	 */
	public static void copyFolderWithSelf(String oldPath, String newPath) {
		try {
			new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件�?
			File dir = new File(oldPath);
			// 目标
			newPath += File.separator + dir.getName();
			File moveDir = new File(newPath);
			if (dir.isDirectory()) {
				if (!moveDir.exists()) {
					moveDir.mkdirs();
				}
			}
			String[] file = dir.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) { // 如果是子文件�?
					copyFolderWithSelf(oldPath + "/" + file[i], newPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final String IMG_CAPTURE_0 = "captured_0.jpg";

	public static final String IMG_FACE_0 = "face_0.jpg";

	public static final String IMG_CAPTURE_1 = "captured_1.jpg";

	public static final String IMG_FACE_1 = "face_1.jpg";

	public String getImgPath(String imgName) {
		return IMG_PATH + File.separator + imgName;
	}

	/**
	 * 获取拍照图像文件路径
	 *
	 * @return
	 */
	public String getFaceImgPath() {
		return IMG_PATH + File.separator + IMG_FACE_0;
	}

	public String getFace1ImgPath() {
		return IMG_PATH + File.separator + IMG_FACE_1;
	}


	/**
	 * 删除文件（包括文件夹和文件）
	 *
	 * @param file
	 */
	public void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteFile(childFiles[i]);
			}
			file.delete();
		}
	}

	public static void deleteQrCode(String fileName) {
		if (fileName == null) {
			return;
		}
		File file = new File(Commons.imagePath);
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			//Log.d("xyc", "deleteQrCode: files[i].getName()="+files[i].getName());
			if (files[i].getName().equals(DEFAULT_IMAGE_NAME) || files[i].getName().equals(fileName+".png")||files[i].getName().equals(fileName+".jpg")) {
				//Log.d("xyc", "deleteQrCode: 不删除的files[i].getName()="+files[i].getName());
			} else {
				files[i].delete();
			}
		}
	}
	//thumb_u.toString(), mMd5Str, imgType,taskId,taskType,this
  public  void  saveImg( String imgUrl, final String fileName, final String  imgType, final String taskId, final String taskType, final ISaveQrCodeDelegate delegate) {
	  Request request = HttpUtils.getAsynchronous(imgUrl);
	  HttpUtils.client.newCall(request).enqueue(new Callback() {
		  @Override
		  public void onFailure(Request request, IOException e) {
			  delegate.saveFailed(e.getMessage(),taskId,taskType);
		  }

		  @Override
		  public void onResponse(Response response) throws IOException {
			  InputStream is = response.body().byteStream();

			  File file = new File(getQrCodeFilepath(fileName)+"."+imgType);
			  FileOutputStream fos = new FileOutputStream(file);
			  try {
				  long sum = 0;
				  byte[] buf = new byte[2048];
				  int len = 0;
				  long total = response.body().contentLength();
				  while (sum < total) {
					  len = is.read(buf);
					  fos.write(buf, 0, len);
					  sum += len;
					  fos.flush();
				  }
			  } catch (Exception e) {
                  delegate.saveFinish(false,taskId,taskType);
			  }
			  delegate.saveFinish(true,taskId,taskType);
		  }
	  });
  }

	public static String getQrCodePath() {
		String path = Commons.imagePath;
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		File[] files = file.listFiles();
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				if (!files[i].getName() .equals(DEFAULT_IMAGE_NAME)) {
					return files[i].getAbsolutePath();
				}
			}
		}
		return Commons.imagePath + "/image_rqCode.png";
	}

	public static String getQrCodeFilepath(String imgName){

		return  Commons.imagePath+ File.separator + imgName;
	}

	public static String getUrlFileName(String url) {
		if (url == null) {
			return null;
		}
		return  MD5Util.hash(url);
		/*url.substring(
				url.lastIndexOf("/") + 1, url.length());*/
	}

	//文件存储根目录
	public static String getFileRoot(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File external = context.getExternalFilesDir(null);
			if (external != null) {
				return external.getAbsolutePath();
			}
		}

		return context.getFilesDir().getAbsolutePath();
	}
	public static void deleteAdOrVideo(){
		List<Ad> adDataList = LogicDbManager.getInstance().getAdDataList();
		if(adDataList==null||adDataList.size()==0){
			return;
		}
		String path = Commons.adDownloadPath;
		File file = new File(path);
		File[] files = file.listFiles();
		if(files==null||files.length==0){
			return;
		}
		for(int i = 0;i<files.length;i++){
			boolean isExists = false;

			for(int j=0;j<adDataList.size();j++){
				String fileName = adDataList.get(j).getFileName();
                 if(fileName==null){
					 return;
				 }
				if (fileName.equals(files[i].getName())) {
					isExists = true;
					break;
				}
			}
			if(!isExists){
				files[i].delete();
			}
		}
	}
}
