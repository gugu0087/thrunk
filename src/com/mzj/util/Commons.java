package com.mzj.util;

import com.mzj.bean.Ad;

import java.util.ArrayList;
import java.util.List;


public class Commons {
  //
  public static String hostIPPrefixes = "http://";

  //测试环境
	public static String hostIp = hostIPPrefixes+"hb.mzjmedia.net";
	public static String WeightDomain = hostIPPrefixes+"health.mzjmedia.net";

  ////	//正式环境
  //public static String hostIp = hostIPPrefixes+"heartbeat.mzjmedia.com";
  //public static String WeightDomain = hostIPPrefixes+"weight.mzjmedia.com";

  //  http://mzjprofiles.natapp1.cc/scan.do?machineId=1&weight=50&appId=wx14f778dbb5744c34&reqTime=1512628291316&delay=0&channel=3
  //二维码生成访问地址
  public static String QRCodeUrl = hostIPPrefixes+"http://mzjprofiles.natapp1.cc/scan.do?";
  //上送体重数据
  public static final String sendWeightUrl = WeightDomain+"/health/weightRecord";

  //获取到随机码之后的，发送数据给后台
  public static final String callBackCode = hostIp+"/app/scale/callBackCode.do";

  //获取二维码
  public static final String getQRCode  = hostIp+"/app/scale/getQRCode.do";

  //心跳，然后获取任务列表
  public static final String theHeartbeatUrl = hostIp+"/heartbeat";

  //发送执行任务结果
  public static final String executeTaskResultUrl = hostIp+"/heartbeat/taskNotify";


  //每次站上电子秤都要发送给后端
  public static final String sendEveryOneStandScale = hostIp+"/app/scale/sendEveryOneStandScale.do";

  //返回给后台，说明当前已经接收到控制状态数据了
  public static final String callBackControlStatu= hostIp+"/app/scale/callBackControlStatu.do";

  //获取广告，包括图片和视频
  public static final String getAdGroupAndApp= hostIp+"/AdGroup/getAdGroupAndApp.do";

  //上传日志地址
  public static final String uploadLog= hostIp+"";


  //app平板当前状态
  public static final String appStatusNormal = "1";      //正常
  public static final String appStatusShutdown = "2";    //关机
  public static final String appStatusReboot = "3";      //重启


  //app控制状态
  public static final String controlType_timingStart = "1";
  public static final String controlType_timingShutdown = "2";
  public static final String controlType_timingReboot = "3";
  public static final String controlType_nowReboot = "4";
  public static final String controlType_nowShutdown = "5";

  //项目文件路径名字
  public static String MZJ = "mzj";
  public static String OLDMZJ = "oldMzj";

  //项目的文件路径
  public static String applicationPath = android.os.Environment.getExternalStorageDirectory().getPath()
          + "/"+MZJ+"/";

  //项目的文件路径
  public static String applicationOldPath = android.os.Environment.getExternalStorageDirectory().getPath()
          + "/grg_balance/";

  //日志路径
  public static String infoPath =  Commons.applicationPath + "log/info/";

  //日志路径
  public static String logPath =  Commons.applicationPath + "log/";
  //日志路径 zip
  public static String logPathZip =  Commons.applicationPath + "log.zip";

  //crash日志
  public static String crashPath = Commons.applicationPath+"log/crashLog/";


  public static String dataPath = Commons.applicationPath+"data/";

  //配置文件路径
  public static String configPath = dataPath + "data.txt";
  //配置文件路径备份文件
  public static String configPathback = dataPath + "data.txt.back";

  //任务列表文件
  public static String taskListFile = dataPath + "taskList.txt";

  //任务执行失败的文件
  public static String failTaskListFile = dataPath + "failTaskList.txt";

  //广告图片文件的初始化路径
  public static String adInitPath = applicationPath + "ad/initAd/";

  //从服务器上下载的广告图片文件存放的路径
  public static String adDownloadPath = applicationPath + "ad/downloadAd/";

  public static String imagePath  = applicationPath + "image";

  //广告图片文件的路径
  public static String adPath = applicationPath + "ad/";

   public static String adDbPath  = applicationPath + "db";
  //视频的路径
  public static String adVideoPath = applicationPath + "ad/video";


  //apk的路径
  public static String apkPath = applicationPath + "apk";

  //apk的路径
  public static String musicPath = applicationPath + "music";

  //app平板上要播放的图片和视频
  public static List<Ad> imageVideoList = new ArrayList<Ad>();

  //app平板上要播放的图片和视频  人站上次播放
  public static List<Ad> adForUserList = new ArrayList<Ad>();

  // 设备Id
  public static String deviceId;

 // 数据库表名
  public static  String AD_TABLE_NAME = "adTable";

  // 心跳请求失败重试次数
  public static final int REQUEST_RETRY_NUMBER = 30;

  // 任务下载图片或视频网络不稳定重试次数
  public static final  int DOWNLOAD_RETRY_NUMBNER =100;


  // 上传体重请求失败重试次数
  public static final int UPDATA_RETRY_NUMBER = 3;

  //发送任务执行结果给 服务端重试次数
  public static final int SEND_TASK_RESULT = 10;


}
