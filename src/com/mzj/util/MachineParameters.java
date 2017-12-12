package com.mzj.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设备参数
 *
 * @author Administrator
 *
 */
public class MachineParameters {

	/**
	 * cpu温度
	 */
	public String getCPUTemperature() {
		FileReader cpuTempfile;
		String str = "";
		try {
			cpuTempfile = new FileReader("/sys/class/thermal/thermal_zone1/temp");
			BufferedReader br = new BufferedReader(cpuTempfile);
			String cuptemp = "";
			while ((cuptemp = br.readLine()) != null) {
				str = cuptemp;
			}
			br.close();
			cpuTempfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	// 内存使用率
	public List<String> getMemoryUsage() {
		List<String> list = new ArrayList<String>();
		FileReader cpuTempfile2;
		String a2 = "";
		try {
			cpuTempfile2 = new FileReader("/proc/meminfo");
			BufferedReader br = new BufferedReader(cpuTempfile2);
			String cuptemp = "";
			while ((cuptemp = br.readLine()) != null) {
				list.add(cuptemp);
				if (list.size() == 2)
					break;
			}
			br.close();
			cpuTempfile2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 *
	 * 获取手机内部可用空间大小
	 *
	 * @return
	 *
	 */
	public long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;

	}

	/**
	 * 获取手机内部空间大小
	 *
	 * @return
	 */
	public long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();// Gets the Android data
		// directory
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize(); // 每个block 占字节数
		long totalBlocks = stat.getBlockCount(); // block总数
		return totalBlocks * blockSize;
	}

	/**
	 * 获取cup试用率
	 * @return
	 */
	public String getCPURateDesc(){
		String path = "/proc/stat";// 系统CPU信息文件
		long totalJiffies[]=new long[2];
		long totalIdle[]=new long[2];
		int firstCPUNum=0;//设置这个参数，这要是防止两次读取文件获知的CPU数量不同，导致不能计算。这里统一以第一次的CPU数量为基准
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		Pattern pattern=Pattern.compile(" [0-9]+");
		for(int i=0;i<2;i++) {
			totalJiffies[i]=0;
			totalIdle[i]=0;
			try {
				fileReader = new FileReader(path);
				bufferedReader = new BufferedReader(fileReader, 8192);
				int currentCPUNum=0;
				String str;
				while ((str = bufferedReader.readLine()) != null&&(i==0||currentCPUNum<firstCPUNum)) {
					if (str.toLowerCase().startsWith("cpu")) {
						currentCPUNum++;
						int index = 0;
						Matcher matcher = pattern.matcher(str);
						while (matcher.find()) {
							try {
								long tempJiffies = Long.parseLong(matcher.group(0).trim());
								totalJiffies[i] += tempJiffies;
								if (index == 3) {//空闲时间为该行第4条栏目
									totalIdle[i] += tempJiffies;
								}
								index++;
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}
					}
					if(i==0){
						firstCPUNum=currentCPUNum;
						try {//暂停50毫秒，等待系统更新信息。
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		double rate=-1;
		if (totalJiffies[0]>0&&totalJiffies[1]>0&&totalJiffies[0]!=totalJiffies[1]){
			double a = totalJiffies[1]-totalIdle[1];
			double b = totalJiffies[0]-totalIdle[0];
			double c = totalJiffies[1]-totalJiffies[0];
			rate=1.0*(a-b)/c;
		}

		return String.format("%.2f",rate);
	}
}
