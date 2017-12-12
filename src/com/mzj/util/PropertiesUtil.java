package com.mzj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Properties读取保存文件类
 *
 * @author fyf000
 *
 */
public class PropertiesUtil {

	public Properties loadConfig(String file) {
		Properties properties = new Properties();
		try {
			FileInputStream s = new FileInputStream(file);
			properties.load(s);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return properties;
	}

	// 保存配置文件
	public boolean saveConfig(String file, Properties properties) {
		// 先备份
		// File
		String str = read(file);
		write(Commons.configPathback,str);
		// 后保存
		FileOutputStream s = null;
		FileOutputStream sBack = null;
		try {
			File fil = new File(file);
			if (!fil.exists())
				fil.createNewFile();
			s = new FileOutputStream(fil);
			properties.store(s, "");

			//再修改备份文件
			File fileBack = new File(Commons.configPathback);
			sBack = new FileOutputStream(fileBack);
			properties.store(sBack, "");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				s.close();
				sBack.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	// 保存配置文件
	public boolean saveConfigNoBack(String file, Properties properties) {
		try {
			File fil = new File(file);
			if (!fil.exists())
				fil.createNewFile();
			FileOutputStream s = new FileOutputStream(fil);
			properties.store(s, "");
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 读取整个文件
	 * @param path
	 * @return
	 */
	public String read(String path) {
		String str = "";
		FileInputStream is = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			is = new FileInputStream(file);
			byte[] b = new byte[is.available()];
			is.read(b);
			str = new String(b);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 写字符串到文件里面
	 * @param path
	 * @param str
	 */
	public void write(String path,String str) {
		File file = new File(path);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(str.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}