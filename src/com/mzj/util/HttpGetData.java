package com.mzj.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.mzj.delegate.ISaveQrCodeDelegate;
import com.mzj.logic.LogicManager;
import com.mzj.scale_app.DeskActivity;
import com.mzj.scale_app.DeskActivityHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HttpGetData implements ISaveQrCodeDelegate {

    private Handler handler;
    private static Activity activity;
    public static int delayTime = 1;   //单位秒
    public static int periodTime = 10; //单位分钟
    public static Timer heartBeatTimer;
    public static TimerTask heartBeatTimerTask;
    private String imgType = "png";
    private String mMd5Str;
    private boolean flag = false;
    private TaskListHelper taskListHelper;
    public HttpGetData() {
    }

    public HttpGetData(Handler handler) {
        this.handler = handler;
    }

    public HttpGetData(Activity activity, Handler handler) {
        this.handler = handler;
        HttpGetData.activity = activity;
    }

    public void sendWeightAndGenerateCode(final String weight,final String type,String appid){
        doSendWeightAndGenerateCode(weight,Commons.UPDATA_RETRY_NUMBER,type,appid);
    }


    // 上送体重，获取随机码或者

    /**
     * @param weight 体重
     * @param num 重试次数
     * @param type 类型
     * @param appid 服务号
     */
    public void doSendWeightAndGenerateCode(final String weight,final int num,final String type,final String appid){
        MzjLog.i("sendWeightAndGenerateCode", "开始上送体重，获取随机码");
        DeskActivity.httpSendWeidhtFlag = true;
        String url = Commons.sendWeightUrl;
        JSONObject params = new JSONObject();
        final String reqTime = new Date().getTime() + "";

        final String deviceId = Commons.deviceId;
        try {
            params.put("machineId", deviceId);
            params.put("weight", weight);
            params.put("reqTime", reqTime);
            params.put("delay", "0");
            params.put("channel", type);
            if(type.equals("2"))
                params.put("officialAccountsServiceAppid",appid);
        } catch (JSONException e1) {
            MzjLog.i("sendWeightAndGenerateCode", "发送体重 设置参数出错");
            e1.printStackTrace();
        }

        Request request = HttpUtils.postAsynchronous(url, params);

        HttpUtils.clientWeight.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request arg0, IOException e) {
                MzjLog.i("发送体重，返回数据，处理异常1", "次数：" + num + "  " + e.getMessage());
                //Log.d("xyc", "onFailure: doSendWeightAndGenerateCode+num=" + num);
                if (num == 1) {
                    DeskActivity.httpSendWeidhtFlag = false;
                    Message msg = new Message();
                    msg.what = 15;
                    msg.obj = "请求响应，返回值异常！";
                    handler.sendMessage(msg);
                } else {
                    int numNew = num;
                    numNew--;
                    doSendWeightAndGenerateCode(weight, numNew,type,appid);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    DeskActivity.httpSendWeidhtFlag = false;
                    if(type.equals("1")) {

                        Message msg = new Message();
                        String map = response.body().string();
                        MzjLog.i("随机码map", map);
                        JSONObject json = new JSONObject(map);
                        String randomCode = json.getString("code");
                        String timeStamp = json.getString("createTime");
                        String createTime = DateUtils.timeStampToStr(new Long(timeStamp));
                        //					String randomCode = "12345";
                        MzjLog.i("返回的验证码randomCode+createTimeToString", randomCode+"==="+createTime);
                        msg.obj = type.equals("2")?weight:randomCode;
                        msg.what = 6;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    MzjLog.i("发送体重，返回数据，处理异常2", e.getMessage());
                }
            }
        });
    }


    /**
     * 初始化本地二维码
     */
    public void getLocalQRCode() {
        BitmapDrawable locBitmapDrawable = null;
        try {
            String qrCodePath = FileUtils.getQrCodePath();
            if(qrCodePath==null){
                return;
            }
            MzjLog.i("getLocalQRCode", "qrCodePath = " + qrCodePath);
            locBitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromPath(qrCodePath);
            MzjLog.i("getLocalQRCode", "locBitmapDrawable = " + locBitmapDrawable);
            Message msg = new Message();
            msg.obj = locBitmapDrawable;
            msg.what = 7;
            handler.sendMessage(msg);
        } catch (Exception e) {
            MzjLog.i("getLocalQRCode", "getLocalQRCode = " + e.getMessage());
        }

    }


    // 获取二维码并显示图片


    /**
     * 获取二维码url
     */
    public void getQRCode(String taskId,String taskType,JSONObject taskDetail) {

        try {
            String qrCodeUrl = taskDetail.getString("img");
            String md5Str = taskDetail.getString("md5");
            if(qrCodeUrl==null||md5Str==null){
                return ;
            }
            mMd5Str = md5Str;
            URL thumb_u = new URL(qrCodeUrl);
           // Log.d("xyc", "getQRCode: thumb_u=" + thumb_u);
            if (thumb_u.toString().contains("png") || thumb_u.toString().contains("PNG")) {
                imgType = "png";
            } else if (thumb_u.toString().contains("jpg") || thumb_u.toString().contains("JPG")||thumb_u.toString().contains("jpeg")) {
                imgType = "jpg";
            }
           // Log.d("xyc", "getQRCode: md5Str=" + mMd5Str);
            FileUtils fileUtils = new FileUtils();
            fileUtils.saveImg(thumb_u.toString(), mMd5Str, imgType,taskId,taskType,this);

            //BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromStream(thumb_u.openStream(), "src");
            // url后面的MD5值 服务端下发的
            //String fileName = FileUtils.getUrlFileName(rqCodeUrl);
            //fileUtils.saveFile(bitmapDrawable,md5Str,imgType);

/*			String qrCodeFilepath = FileUtils.getQrCodeFilepath(md5Str+"."+imgType); // 本地二维码的路径
            Log.d("xyc", "getQRCode: qrCodeFilepath="+qrCodeFilepath);
			boolean md5CheckPass = FileMD5Compare.verifyInstallPackage(qrCodeFilepath, md5Str);
			Log.d("xyc", "getQRCode: md5CheckPass="+md5CheckPass);
			if(!md5CheckPass){
				return false;
			}
			BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromPath(qrCodeFilepath);
			FileUtils.deleteQrCode(md5Str);
			Message msg = new Message();
			msg.obj = bitmapDrawable;
			msg.what = 7;
			handler.sendMessage(msg);*/

        } catch (Exception e) {
            MzjLog.i("doGetQRCode：", e.getMessage());
            BitmapDrawable locBitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromPath(Commons.imagePath + "/image_rqCode.png");
            Message msg = new Message();
            msg.obj = locBitmapDrawable;
            msg.what = 7;
            handler.sendMessage(msg);
            MzjLog.i("doGetQRCode：", "从网络端更新二维码失败，发送广播！");
            if(taskListHelper==null){
                taskListHelper =  new TaskListHelper(activity, handler);
            }
            taskListHelper.sendTaskResult(false, taskType, taskId, null,Commons.SEND_TASK_RESULT);
        }
    }

    @Override
    public void saveFinish(boolean isFinish,String taskId,String taskType) {
        if (!isFinish) {
            return;
        }
        String qrCodeFilepath = FileUtils.getQrCodeFilepath(mMd5Str + "." + imgType); // 本地二维码的路径
        boolean md5CheckPass = FileMD5Compare.verifyInstallPackage(qrCodeFilepath, mMd5Str);

        if (!md5CheckPass) {
            FileUtils.deleteQrCode("");
            flag = false;
        }else {
            FileUtils.deleteQrCode(mMd5Str);

            BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromPath(qrCodeFilepath);
            Message msg = new Message();
            msg.obj = bitmapDrawable;
            msg.what = 7;
            handler.sendMessage(msg);
            flag = true;
           // PreferenceUtils.getInstance().putPreString(Commons.MZJ_QR_CODE,qrCodeFilepath,activity.getApplicationContext());
        }
       if(taskListHelper==null){
           taskListHelper =  new TaskListHelper(activity, handler);
       }
        taskListHelper.sendTaskResult(flag, taskType, taskId, null,Commons.SEND_TASK_RESULT);

        MzjLog.i("doGetQRCode：", "从网络端更新二维码成功，发送广播！");
    }

    @Override
    public void saveFailed(String msg,String taskId,String taskType) {

        if(taskListHelper==null){
            taskListHelper =  new TaskListHelper(activity, handler);
        }
        MzjLog.i("xyc", "getQRCode-saveFailed: taskType="+taskType+" taskId="+taskId);
        taskListHelper.sendTaskResult(false, taskType, taskId, null,Commons.SEND_TASK_RESULT);
        MzjLog.i("saveQrCodeFailed",msg);
    }

    /**
     * 上送设备状态。即是上送心跳包
     *
     * @param
     */
    public void getDeviceStatusAndControlApp() {


        final PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        // 5分钟随机发一次
        heartBeatTimer = new Timer();
        heartBeatTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                //休眠状态下不发送数据
                String controlDeviceStatus = ControlAppUtil.controlDeviceStatus;
                if (controlDeviceStatus != null) {
                    if (controlDeviceStatus.equals(Commons.controlType_timingShutdown) || controlDeviceStatus.equals(Commons.controlType_nowShutdown)) {
                        if (powerManager.isScreenOn() == false) {
                            return;
                        }
                    }
                }
                doGetDeviceStatusAndControlApp(Commons.REQUEST_RETRY_NUMBER); // 心跳上送 失败重新上传次数30次
            }
        }, delayTime, periodTime * 60 * 1000);
//		 }, 1, 3000000);
    }

    /**
     * 重设心跳时间
     */
    public void reSetHeartbeatTime(int delay, int interval) {
        if (heartBeatTimer != null) {
            heartBeatTimer.cancel();
            heartBeatTimer = null;
        }

        if (heartBeatTimerTask != null) {
            heartBeatTimerTask.cancel();
            heartBeatTimerTask = null;
        }
        delayTime = delay;
        periodTime = interval;
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getDeviceStatusAndControlApp();
    }

    /**
     * 心跳上送
     *
     * @param num 失败重试次数
     */
    public void doGetDeviceStatusAndControlApp(final int num) {
        String url = Commons.theHeartbeatUrl;
        JSONObject params = new JSONObject();
        // 设备id
        try {
            params.put("machineId", Commons.deviceId);
            params.put("status", DeskActivity.deviceStatus);
            params.put("reqTime", new Date().getTime() + "");
            params.put("version", ApkInfo.getApkInfoVersionCode());
            //经纬度
            params.put("longitude", DeskActivity.longitude);
            params.put("latitude", DeskActivity.latitude);

            //cup使用率，温度
            MachineParameters m = new MachineParameters();
            JSONObject cpu = new JSONObject();
            cpu.put("rate", m.getCPURateDesc());
            cpu.put("temperature", m.getCPUTemperature());
            params.put("cpu", cpu);

            //磁盘总大小，剩余大小
            JSONObject disk = new JSONObject();
            disk.put("total", m.getTotalInternalMemorySize());
            disk.put("surplus", m.getAvailableInternalMemorySize());
            params.put("disk", disk);

            JSONObject memory = new JSONObject();
            memory.put("total", m.getMemoryUsage().get(0).trim().split(":")[1].trim());
            memory.put("surplus", m.getMemoryUsage().get(1).trim().split(":")[1].trim());
            params.put("ram", memory);


            //串口状态
            JSONObject serialPort = new JSONObject();
            serialPort.put("serial2", DeskActivity.deviceStatus);
            params.put("serialPort", serialPort);

            JSONObject net = new JSONObject();

            net.put("type", DeskActivityHelper.NetType);
            net.put("modal", DeskActivityHelper.modal);
            net.put("sign", DeskActivityHelper.sign);
            params.put("net", net);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }


        Request request = HttpUtils.postAsynchronous(url, params);
        HttpUtils.client.newCall(request).enqueue(new Callback() {



            @Override
            public void onFailure(Request arg0, IOException arg1) {
                MzjLog.i("doGetDeviceStatusAndControlApp", "上送设备心跳失败" + arg1.getMessage());
                Log.d("xyc", "onFailure: doGetDeviceStatusAndControlApp+arg1="+arg1.getMessage());
                if (num == 1) {
                    DeskActivity.httpSendWeidhtFlag = false;
                    Message msg = new Message();
                    msg.what = 15;
                    msg.obj = "请求响应，返回值异常！";
                    handler.sendMessage(msg);
                } else {
                    int numNew = num;
                    numNew--;
                    doGetDeviceStatusAndControlApp(numNew);
                }

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonString = response.body().string();

                MzjLog.i("doGetDeviceStatusAndControlApp", "上送设备心跳" + jsonString);
                Log.d("xyc", "onResponse: jsonString="+jsonString);
                if (jsonString.isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LogicManager.getInstance().getReloadAdList();
                        }
                    }).start();
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    taskListHelper = new TaskListHelper(activity, handler);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String taskId = json.getString("id");
                        String taskType = json.getString("type");
                        String taskDetailStr = json.getString("taskDetail");
                        //执行任务
                        Log.d("xyc", "onResponse: taskId"+taskId);
                        boolean flag = taskListHelper.executeTheTask(taskId, taskType, taskDetailStr);
                        Log.d("xyc", "onResponse: flag="+flag);
                        //发送结果
                        boolean condition = !TaskListHelper.timeSwitch.equals(taskType);
                        condition = condition && !TaskListHelper.updateAPK.equals(taskType);
                        condition = condition && !TaskListHelper.reBoot.equals(taskType);
                        condition = condition && !TaskListHelper.screenBrightness.equals(taskType);
                        condition = condition && !TaskListHelper.ad.equals(taskType);
                        condition = condition && !TaskListHelper.QRCode.equals(taskType);

                        Log.d("xyc", "onResponse: condition="+condition);
                        Log.d("xyc", "onResponse: flag="+flag);
                        if (condition) {
                            taskListHelper.sendTaskResult(flag, taskType, taskId, null,Commons.SEND_TASK_RESULT);
                        }
                    }
                } catch (Exception e) {
                    MzjLog.i("onResponse =","返回json数据处理异常="+e.getMessage());
                }
            }
        });
    }


    /**
     * 定时发送因为网络异常，没有发送到服务器的体重
     *
     * @param weight
     * @param infoDao
     * @param d
     */
/*    public void doSendWeightTiming(Weight weight, final InfoDao infoDao, final DeskActivityHelper d) {

        final int id = weight.getId();
//		String machineId = weight.getMachineId();
        String weightStr = weight.getWeight();
//		String reqTime = weight.getReqTime();
        String url = Commons.sendWeightUrl;
        JSONObject params = new JSONObject();

        String deviceId = Commons.deviceId;
        try {
            params.put("machineId", deviceId);
            params.put("weight", weightStr);
            String timestamp = new Date().getTime() + "";
            params.put("reqTime", timestamp);
            params.put("delay", "1");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        Request request = HttpUtils.postAsynchronous(url, params);
        HttpUtils.clientWeight.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request arg0, IOException arg1) {
                int i = 0;
                MzjLog.i("aaa", "aa");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                infoDao.del(id + "");
                d.doQueryData();
            }
        });
    }*/



}
