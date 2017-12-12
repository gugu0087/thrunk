package com.mzj.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class FileMD5Compare {

	public static boolean verifyInstallPackage(String packagePath, String crc) {
		File file = new File(packagePath);
		if(!file.exists()){
			return false;
		}
		String FileSrc = getFileMd5(packagePath);
		MzjLog.i("xyc","FileSrc的md5="+FileSrc);
		if(FileSrc == null){
			FileSrc = crc;
		}
		crc = crc.toLowerCase();
        // 比较两个文件的MD5值，如果一样则返回true
        return FileSrc.equals(crc);
	}


	public static String getFileMd5(String packagePath){
		String digestStr = "";
		File file = new File(packagePath);
		InputStream signedData = null;
		try {
			if (!file.isFile()) {
				return digestStr;
			}
			MessageDigest digest = null;
			FileInputStream in = null;
			byte buffer[] = new byte[1024];
			int len;
			try {
				digest = MessageDigest.getInstance("MD5");
				in = new FileInputStream(file);
				while ((len = in.read(buffer, 0, 1024)) != -1) {
					digest.update(buffer, 0, len);
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			byte[] digestByte = digest.digest();
			digestStr = bytesToHexString(digestByte);// 将得到的MD5值进行移位转换
			digestStr = digestStr.toLowerCase();
		} catch (Exception e) {
			return "";
		}
		finally{
			if(signedData!=null){
				try {
					signedData.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return digestStr;
	}

	// bytesToHexString MD5值移位转换
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		int i = 0;
		while (i < src.length) {
			int v;
			String hv;
			v = (src[i] >> 4) & 0x0F;
			hv = Integer.toHexString(v);
			stringBuilder.append(hv);
			v = src[i] & 0x0F;
			hv = Integer.toHexString(v);
			stringBuilder.append(hv);
			i++;
		}
		return stringBuilder.toString();
	}

}
