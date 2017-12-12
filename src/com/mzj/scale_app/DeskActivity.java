package com.mzj.scale_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.grg.weight.R;
import com.mzj.bean.Ad;
import com.mzj.crashHandler.CrashActivityList;
import com.mzj.crashHandler.CrashApplication;
import com.mzj.service.LocationService;
import com.mzj.util.ApkInfo;
import com.mzj.util.Commons;
import com.mzj.util.DataFormUtil;
import com.mzj.util.DateUtils;
import com.mzj.util.FileMD5Compare;
import com.mzj.util.FileUtils;
import com.mzj.util.HttpGetData;
import com.mzj.util.MachineParameters;
import com.mzj.util.MusicThread;
import com.mzj.util.MyTimerTask;
import com.mzj.util.MzjLog;
import com.mzj.util.SharedPreferencesUtils;
import com.mzj.util.ZXingUtilssss;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android_serialport_api.SerialPort;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class DeskActivity extends Activity {
	public static ImageView signImage,mobileImage;//信号图片
	//	private static GifImageView gifImageViewStandOn;  //人站到秤上的gif或者图片
	private static GifImageView gifImageView1;  //第一张gif或者图片
	private static GifImageView gifImageView2;  //第二张gif或者图片
	private static GifImageView gifImageView3;  //第三张gif或者图片
	private static GifImageView gifImageView4;  //第四张gif或者图片
	private static GifImageView wheel;//转动的动画
	private static List<Ad> imageVideoList = new ArrayList<Ad>();// 图片视频数据集合
	private static List<Ad> adForUserList = new ArrayList<Ad>();// 图片视频数据集合
	//	public static String oldAdFileName = "";
	private static int playCurrentIndex = -1;// 当前要播放或者在播放的视频或者图片的位置
	private static int playStandOnCurrentIndex = 0;//人站上去播放的视频或者图片的位置
	private static int playStandOnCurrentIndexFlag = 0;
	private static boolean isPlayStandOn = false;

	private static int delay = 2000;// 视频延时开始时间 秒为单位
	private static int weightDelay = 30000;//右边称重延时时间
	private static int imageUseTime = 0; // 图片上次已经显示的时间
	private static MyTimerTask advTimerTask; // 定时播放视频或者图片
	private static MyTimerTask advTimerTaskStanOn; // 定时播放视频或者图片
	//	private static MyTimerTask imageTimerTask;// 定时播放图片动画
	private static Bitmap bitmap;
	private static Date startTime;
	private static int playStatus = 0; // 播放的状体，0表示播放主页
	private static int pageType = 0; // 0表示上传，1表示直接显示体重
	private static String appid;//服务号ID

	private static SharedPreferencesUtils spf ;

	public static MediaPlayer mediaPlayerSv,mediaPlayerStandOn;
	private static boolean mediaplayerPause = false;
	private static SurfaceView surfaceViewSv;//平时播放的视频
	private static SurfaceView surfaceViewStandOn;//人站上称的视频
	private static boolean imageAnimationPlayingStop = false;


	private static Activity thisContext;
	private static TextView textView_randomCode,versionNum,hynText;

	private static ImageView image_right,ivCodeBg;

	FileOutputStream mOutputStream;
	FileInputStream mInputStream;
	SerialPort sp;


	public static TextView text_scan,text_weight,text_deviceId;
	private static FrameLayout frameLayout_qrCodeBig;
	private static LinearLayout linearLayout_qrCodeSmall,frameLayout_wheel,signLayout;
	public static LinearLayout linearLayout_downPart,linearLayout_network,linearLayout_midller,frameLayout_staticwheel;
	private View view_line;
	public static boolean netWorkStatus = true;
	private int threeTime = 0;
	private static MusicThread musicThread;
	private static boolean uartFlag =false;

	// 对话框提示
	AlertDialog.Builder builder = null;
	Dialog dialog = null;

	private static long scaleBeginTime = 0;
	public static String deviceStatus = "1";
	public static MyTimerTask whileTimerTask;

	// 广播
	//private NetworkConnectChangedReceiver mRecevier;


	//定位功能aa
	public static LocationService locationService;
	public static String latitude="";//维度
	public static String longitude="";//经度
	public static boolean httpSendWeidhtFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_desk);
		MzjLog.i("  desk init", "esk init");
		init();//初始化函数
		MzjLog.i(" desk initActivity", "desk initActivity");
		initActivity();
		MzjLog.i("desk  initActivity end", "desk  initActivity end");
		//设置声音大小
		AudioManager audioManager =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxStreamSystem  = audioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM , maxStreamSystem, AudioManager.FLAG_PLAY_SOUND);
		MzjLog.i("audioManager", "audioManager");

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onStart() {
		super.onStart();
		// 注册一个监听网络连接情况的广播
/*		mRecevier = new NetworkConnectChangedReceiver();
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mRecevier, filter);*/

		//定位相关配置
		locationService = CrashApplication.locationService;
		locationService.registerListener(mListener);
		//使用默认的配置
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}
		locationService.start();
	}

	@Override
	protected void onResume() {
		//设置状态
		changePages();
		super.onResume();
	}
	public void init() {

		surfaceViewSv = (SurfaceView) findViewById(R.id.surfaceViewSv);
		surfaceViewStandOn = (SurfaceView) findViewById(R.id.surfaceViewStandOn);

		gifImageView1 = (GifImageView) findViewById(R.id.gifImageView1);
		gifImageView2 = (GifImageView) findViewById(R.id.gifImageView2);

		gifImageView3 = (GifImageView) findViewById(R.id.gifImageView3);
		gifImageView4 = (GifImageView) findViewById(R.id.gifImageView4);

		signImage = (ImageView) findViewById(R.id.signImage);
		mobileImage = (ImageView) findViewById(R.id.mobileImage);
		ivCodeBg = (ImageView) findViewById(R.id.ivCodeBg);


		text_scan = (TextView) findViewById(R.id.text_scan);
		text_weight = (TextView) findViewById(R.id.text_weight);
		hynText = (TextView) findViewById(R.id.hynText);
		frameLayout_qrCodeBig = (FrameLayout) findViewById(R.id.frameLayout_qrCodeBig);
		frameLayout_wheel = (LinearLayout) findViewById(R.id.frameLayout_wheel);
		frameLayout_staticwheel = (LinearLayout) findViewById(R.id.frameLayout_staticwheel);
		signLayout = (LinearLayout) findViewById(R.id.signLayout);

		wheel = (GifImageView)findViewById(R.id.giv_staticbird);
		wheel.setImageResource(R.drawable.wheel);


		linearLayout_qrCodeSmall = (LinearLayout) findViewById(R.id.linearLayout_qrCodeSmall);
		linearLayout_network = (LinearLayout) findViewById(R.id.linearLayout_network);
		linearLayout_downPart = (LinearLayout) findViewById(R.id.linearLayout_downPart);
//zhongjian bufen
		linearLayout_midller= (LinearLayout) findViewById(R.id.linearLayout_midller);
		text_deviceId=(TextView) findViewById(R.id.text_deviceId);

		textView_randomCode = (TextView) findViewById(R.id.textView_randomCode);
		versionNum = (TextView) findViewById(R.id.versionNum);
		versionNum.setText(ApkInfo.getApkInfoVersionCode());

		hynText = (TextView) findViewById(R.id.hynText);
//		text_deviceNum = (TextView) findViewById(R.id.text_deviceNum);

		// 获取二维码并显示图片   大图二维码和小图二维码
		image_right = (ImageView) findViewById(R.id.image_right);
		//此句可能需要重新授权

//		image_right.setBackground((BitmapDrawable) BitmapDrawable.createFromPath(Commons.imagePath+"/image_rqCode.png"));

		//媒体播放器
		mediaPlayerSv = new MediaPlayer(); //平时播放的视频
		mediaPlayerStandOn = new MediaPlayer();//人站上去的视频

//		//语音播放线程
		musicThread = new MusicThread(this);
		musicThread.start();

		// 定时播放视频和图片,
		advTimerTask = new MyTimerTask(handler, delay, 2);
		advTimerTask.startTimer();
	}


	public void initActivity(){

		//獲取本地狀態
		spf= new SharedPreferencesUtils(CrashApplication.getAppContext());
		pageType =spf.getInt("pageType");
	//pageType=2;

		appid=spf.getString("appid", "wx14f778dbb5744c34");
		DeskActivityHelper dah = new DeskActivityHelper(this,handler);
		dah.init();

		// 屏幕常亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//添加到容器
		CrashActivityList.addAcitivity(this);
		//初始化串口通信
		initSerialPortCommunity();
		//串口断开检测
//		checkPort(); //20170915 屏蔽串口通信的检测

		getDeviceStatusAndControlApp(null);

		thisContext = this;
		text_deviceId.setText("设备号："+ Commons.deviceId);
		imageVideoList = Commons.imageVideoList;
		adForUserList = Commons.adForUserList;
	}
	private static void changePages(){
		MzjLog.i("xyc", "changePages+pageType="+pageType);
		linearLayout_qrCodeSmall.setVisibility(View.GONE);
		frameLayout_qrCodeBig.setVisibility(View.VISIBLE);
		frameLayout_wheel.setVisibility(View.GONE);
		text_weight.setVisibility(View.VISIBLE);
		linearLayout_downPart.setBackgroundResource(R.drawable.word_line);
		switch (pageType){
			case 1://直接显示体重
				weightDelay=20000;
				ivCodeBg.setVisibility(View.GONE);
				hynText.setVisibility(View.VISIBLE);
				image_right.setVisibility(View.GONE);
				linearLayout_downPart.setVisibility(View.INVISIBLE);
				linearLayout_midller.setVisibility(View.VISIBLE);
				frameLayout_staticwheel.setVisibility(View.VISIBLE);
				linearLayout_qrCodeSmall.setBackground(null);
//				hynText.setTextColor(thisContext.getResources().getColor(R.color.title_hint_color));
				hynText.setText(R.string.mzjwelcome);
				break;
			case 2://扫一扫获取体重
				weightDelay=30000;
				hynText.setVisibility(View.VISIBLE);
				ivCodeBg.setVisibility(View.GONE);
				image_right.setVisibility(View.GONE);
				hynText.setText(R.string.mzjwelcome);

//				changeTestSize(hynText,3,hynText.getText().length());
				linearLayout_midller.setVisibility(View.VISIBLE);
				frameLayout_staticwheel.setVisibility(View.VISIBLE);
//				hynText.setTextColor(thisContext.getResources().getColor(R.color.white));
				text_scan.setText(R.string.hint_one);
				text_weight.setText(R.string.hint_two);
				linearLayout_downPart.setVisibility(View.VISIBLE);
				break;

			default://扫码关注公众号
				pageType=0;

				//获取本地二维码
				HttpGetData httpGetData = new HttpGetData(handler);
				httpGetData.getLocalQRCode();

				weightDelay=30000;
				text_scan.setText(R.string.scan);
				text_weight.setText(R.string.weight);
				ivCodeBg.setVisibility(View.GONE);
				hynText.setVisibility(View.GONE);
				image_right.setVisibility(View.VISIBLE);
				linearLayout_downPart.setVisibility(View.VISIBLE);
				linearLayout_midller.setVisibility(View.VISIBLE);
				frameLayout_staticwheel.setVisibility(View.VISIBLE);
				linearLayout_qrCodeSmall.setBackground(DeskActivity.thisContext.getResources().getDrawable(R.drawable.number_bg_new));
				break;
		}
	}


	public static void setVolumeUp(){
		mediaPlayerSv.setVolume(MusicThread.volume, MusicThread.volume);
		mediaPlayerStandOn.setVolume(MusicThread.volume, MusicThread.volume);
	}

	public static void setVolumeDown(){
		mediaPlayerSv.setVolume(0, 0);
		mediaPlayerStandOn.setVolume(0, 0);
		musicThread.setVolumeDown();
	}

	public static void setDrawable(ImageView imageView,Drawable bitmapDrawable){
		Bitmap bitmap =  imageView.getDrawingCache();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
		imageView.setImageDrawable(bitmapDrawable);
	}
	/**
	 * 显示dialog
	 *
	 * @param context
	 * @param title
	 * @param msg
	 */
	public void showDialog(Context context, String title, String msg) {
		if (builder == null) {
			builder = new AlertDialog.Builder(context);
		}
		builder.setTitle(title);
		builder.setMessage(msg + "!");
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		dialog.show();
	}

	//读取串口数据
	public void initSerialPortCommunity(){

		try {
			sp = new SerialPort(new File("/dev/ttyMT2"), 9600, 0);
			mOutputStream = (FileOutputStream) sp.getOutputStream();
			mInputStream = (FileInputStream) sp.getInputStream();
			mOutputStream.write(11);
		} catch (Exception e) {
			e.printStackTrace();
			uartFlag = true;
			showDialog(this,"提示","请检查电子秤连接，正确连接后重新打开app应用");
			new Thread(){
				public void run(){
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}




		new Thread(){
			public void run(){
				try {
					if(uartFlag == true)return;
					byte[] buffer = new byte[10];
					boolean isRepeat = false;
					int len = 0;
					int zblen = 0;
					int weight = 0;
					MachineParameters mp = new MachineParameters();
					while(true){
						if (mInputStream == null){
							return;
						}
						int size = mInputStream.read(buffer);   //这里会阻塞
						threeTime = 0;

						if(size > 0){
							//MzjLog.i("====开始播放gif size",size+"");
							String strHex = DataFormUtil.ByteArrToHex(buffer);
							scaleBeginTime = new Date().getTime();
							deviceStatus = "1";
							if(strHex.indexOf("CA 03 5A A5")==0){
								sendMessage(14);
								continue;
							}

							len  = strHex.indexOf("CA 01");//稳定的数据
							zblen = strHex.indexOf("CA 00");//没有人称重的数据
							//MzjLog.i("====开始播放gif strHex",strHex+"");
							if(frameLayout_wheel.getVisibility()==View.GONE && zblen == 0){
								//MzjLog.i("====开始播放gif","2");
								sendMessage(9);
							}
							List<String> list = mp.getMemoryUsage();
							if(zblen==0){
								//MzjLog.i("====开始播放gif  zblen",""+" "+list.get(0)+"   "+list.get(1));
								isRepeat = false;
							}
							if(len == 0){

								if((!isPlayStandOn)&&adForUserList.size()>0){
									playStandOnCurrentIndexFlag++;
									Message msg = new Message();
									msg.obj = playStandOnCurrentIndexFlag;
									msg.what = 23;
									handler.sendMessage(msg);
								}
								len = len +4;
								String hex = strHex.substring(len+2, len+4)+strHex.substring(len+5, len+7);
								//十六进制数据转成10进制数据
								weight = Integer.parseInt(hex,16);
								double weightDouble = (double)weight/100;

								//MzjLog.i("====开始播放gif  有稳定数据","===有稳定数据"+" "+list.get(0)+"   "+list.get(1));

								if((!isRepeat)&& DateUtils.isCheckedDouble()){

									MzjLog.i("====开始播放gif  发送稳定数据","===发送稳定数据"+" "+list.get(0)+"   "+list.get(1));


									String weightString = weightDouble+"";
									if(!weightString.equals("")){
										if(pageType==1){//直接显示

											Message msg = new Message();
											msg.obj = weightString;
											msg.what = 6;
											handler.sendMessage(msg);
										}else if(pageType==2){//扫码

											Message msg = new Message();
											msg.obj = weightString;
											msg.what = 6;
											handler.sendMessage(msg);

											Message msg2 = new Message();
											msg2.obj = weightString;
											msg2.what = 8;
											handler.sendMessage(msg2);
										}else{//绑定订阅号

											Message msg = new Message();
											msg.obj = weightString;
											msg.what = 8;
											handler.sendMessage(msg);
										}

									}
									isRepeat = true;
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}.start();



		//3秒后发现 串口还是阻塞的定时任务
		new Thread(){
			public void run(){
				while(true){
					threeTime++;
					//	Log.i("threeTime", threeTime+"");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(threeTime>=3){
						threeTime = 0;
						Message msg = new Message();
						msg.what = 11;
						handler.sendMessage(msg);
					}
				}
			}
		}.start();
	}


	//串口是否端口检查
	public static void checkPort(){
		new Thread(){
			public void run(){
				while(true){
					long timeInterval = 1000*60*62;
//							long timeInterval = 1000*2;
					try {
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long nowTime = new Date().getTime();
					if(scaleBeginTime==0 ||nowTime - scaleBeginTime >= timeInterval){
						deviceStatus = "0";
						Message message = new Message();
						message.what = 13;
						handler.sendMessage(message);
					}else{
					}
				}
			}
		}.start();

	}



	/**
	 * dd 加载视频或者图片
	 *
	 * @param
	 */
	public static void playVideoOrImage() {
		// 主页面隐藏
		hideImageVideo();
		// 播放到最后，重头开始播放
		if (playCurrentIndex >= imageVideoList.size()) {
			playCurrentIndex = 0;
			surfaceViewSv.setVisibility(View.GONE);
			imageVideoList = Commons.imageVideoList;
		}
		if(imageVideoList==null||imageVideoList.size()==0){
			return;
		}
		Ad ad = imageVideoList.get(playCurrentIndex);
		String fileType = ad.getFileType();
		if (fileType.contentEquals(Ad.VIDEOTYPE)) {
			surfaceViewSv.setVisibility(View.VISIBLE);
			// 播放视频状态
			playStatus = 1;
			surfaceViewSv.setAlpha(1);
			if (mediaplayerPause == true) {
				mediaplayerPause = false;
				mediaPlayerSv.start();
			} else {
				mediaplayerPause = false;
				playVideo(ad.getFilePath(),mediaPlayerSv);
			}
		} else {
			// 设置为没有暂停状态
			imageAnimationPlayingStop = false;
			// 播放图片状态
			playStatus = 2;
			// 如果上一个是视频，那么就隐藏视频
			if (playCurrentIndex - 1 >= 0) {
				ad = imageVideoList.get(playCurrentIndex - 1);
				if (ad.getFileType().contentEquals(Ad.VIDEOTYPE)) {
					surfaceViewSv.setVisibility(View.INVISIBLE);
				}
			}

			ad = imageVideoList.get(playCurrentIndex);

			String fileName1 = ad.getFilePath();
			String fileName2 = ad.getFilePath();

			setGifImageView(fileName1,gifImageView1);
			setGifImageView(fileName2,gifImageView2);

			gifImageView1.setVisibility(View.VISIBLE);
			gifImageView2.setVisibility(View.VISIBLE);


			// 计算图片播放时间
			Integer duration = new Integer(ad.getDuration())*1000;
			int imageDelay =  duration - imageUseTime; // 图片显示的时间=
			imageUseTime = 0;															// 后台设置的时间-上次图片显示的时间
			startTime = new Date();
//			if (imageTimerTask != null) {
//				imageTimerTask.stopTimer();
//			}
			// 定时播放视频和图片
			if (imageDelay <= 0) {
				imageDelay = 0;
			}
//			playCurrentIndex++;
			advTimerTask.addTimeTask(imageDelay);
		}

	}



	/**
	 * 加载视频或者图片，人站上电子秤上
	 *
	 * @param index
	 */
	public static void playStandOn(int index) {
		isPlayStandOn = true;
		if(index!=-1){
			pausePlay(null);
			if(index>adForUserList.size()){
				playStandOnCurrentIndexFlag = 1;
				playStandOnCurrentIndex = 0;
			}else{
				playStandOnCurrentIndex = index-1;
			}
		}

		// 播放到最后，重头开始播放
		if (playStandOnCurrentIndex >= adForUserList.size()) {
			playStandOnCurrentIndex = 0;
			surfaceViewStandOn.setVisibility(View.GONE);
			gifImageView3.setVisibility(View.GONE);
			gifImageView4.setVisibility(View.GONE);
			adForUserList = Commons.adForUserList;
			//播放没人站上去的图片
			isPlayStandOn = false;
			playVideoOrImage();
			return;
		}

		Ad ad = adForUserList.get(playStandOnCurrentIndex);
		String fileType = ad.getFileType();
		if (fileType.contentEquals(Ad.VIDEOTYPE)) {
			gifImageView3.setVisibility(View.GONE);
			gifImageView4.setVisibility(View.GONE);
			surfaceViewStandOn.setVisibility(View.VISIBLE);
			// 播放视频状态
//			playStatus = 1;
			surfaceViewStandOn.setAlpha(1);
//			if (mediaplayerPause == true) {
//				mediaplayerPause = false;
//				mediaPlayerSv.start();
//			} else {
//				mediaplayerPause = false;
//				playVideo(ad.getFileName(),mediaPlayerSv);
//			}
			playVideo(ad.getFilePath(),mediaPlayerStandOn);
		} else {
			// 设置为没有暂停状态
//			imageAnimationPlayingStop = false;
			// 播放图片状态
//			playStatus = 2;
			// 如果上一个是视频，那么就隐藏视频
			if (playStandOnCurrentIndex - 1 >= 0) {
				ad = adForUserList.get(playStandOnCurrentIndex - 1);
				if (ad.getFileType().contentEquals(
						Ad.VIDEOTYPE)) {
					surfaceViewStandOn.setVisibility(View.INVISIBLE);
				}
			}

			ad = adForUserList.get(playStandOnCurrentIndex);

			String fileName1 = ad.getFilePath();
			String fileName2 =ad.getFilePath();
			setGifImageView(fileName1,gifImageView3);
			setGifImageView(fileName2,gifImageView4);

			gifImageView3.setVisibility(View.VISIBLE);
			gifImageView4.setVisibility(View.VISIBLE);


			// 计算图片播放时间
			Integer duration = new Integer(ad.getDuration())*1000;
//			int imageDelay =  duration - imageUseTime; // 图片显示的时间=
//																		// 后台设置的时间-上次图片显示的时间
//			startTime = new Date();
////			if (imageTimerTask != null) {
////				imageTimerTask.stopTimer();
////			}
//			// 定时播放视频和图片
//			if (imageDelay <= 0) {
//				imageDelay = 0;
//			}
			playStandOnCurrentIndex++;
			if(advTimerTaskStanOn == null){
				advTimerTaskStanOn = new MyTimerTask(handler, duration, 23,-1);
			}
			advTimerTaskStanOn.addTimeTask(duration);
		}

	}

	private static class MyCallBack implements SurfaceHolder.Callback {
		private MediaPlayer mediaPlayer;

		public MyCallBack(MediaPlayer mediaPlayer) {
			this.mediaPlayer = mediaPlayer;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mediaPlayer.setDisplay(holder);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	}


	/**
	 * 播放视频
	 *
	 * @param path
	 */
	protected static void playVideo(String path,final MediaPlayer mediaPlayer) {

		// 获取视频文件地址
		File file = new File(path);
		if (!file.exists()) {
			surfaceViewSv.setVisibility(View.GONE);
			return;
		}
		surfaceViewSv.setVisibility(View.VISIBLE);
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置播放的视频源
			mediaPlayer.setDataSource(path);
			// 设置显示视频的SurfaceHolder
			SurfaceHolder surfaceHolder= null;
			if(mediaPlayer == mediaPlayerSv) {
				surfaceHolder = surfaceViewSv.getHolder();
				//mediaPlayer.setDisplay(surfaceHolder);
				surfaceHolder.addCallback(new MyCallBack(mediaPlayer));
			}else {

				surfaceHolder = surfaceViewStandOn.getHolder();
				//mediaPlayer.setDisplay(surfaceHolder);
				surfaceHolder.addCallback(new MyCallBack(mediaPlayer));
			}

//			mediaPlayer.prepareAsync();
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
//					if (imageAnimationPlayingStop == true)
//						return;
					mediaPlayer.start();
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// 在播放完毕被回调
					if(mediaPlayer == mediaPlayerSv) {
						playCurrentIndex++;
						playVideoOrImage();
					}else {
						playStandOnCurrentIndex++;
						surfaceViewStandOn.setVisibility(View.GONE);
						playStandOn(-1);
					}

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 2:
					// 播放视频或者图片
					imageAnimationPlayingStop = false;
					playCurrentIndex++;
					playVideoOrImage();
					break;
				case 3:
					// 暂停处理
					pausePlay(null);
					break;
				case 6:
					//随机码设置

					standed(String.valueOf(msg.obj));
					break;
				case 7:
					//二维码设置
					Drawable drawable = (Drawable) msg.obj ;
					if (drawable == null) {
						image_right.setImageResource(R.drawable.imagerqcode);
					} else {
//						image_right.setBackground(drawable);
						setDrawable(image_right,drawable);
					}
					MzjLog.i("二维码设置drawable", "drawable="+drawable);
//				image_right_rqCode.setBackground(drawable);
					break;
				case 8:
					String weight = (String) msg.obj;
					HttpGetData httpGetData = new HttpGetData(thisContext,handler);
					httpGetData.sendWeightAndGenerateCode(weight,pageType==2?"2":"1",appid);
					break;
				case 9:
					//隐藏二维码，开始播放gif,同时播放语音环节
					//MzjLog.i("隐藏二维码，开始播放gif,同时播放语音环节","1");
//				frameLayout_qrCodeBig.setVisibility(View.GONE);
					standing();

					break;
				case 10:
//				MzjLog.i("30秒后转换回开始页面","1");
					//30秒后转换回开始页面
					//if(((String)msg.obj).equals(textView_randomCode.getText()) && linearLayout_qrCodeSmall.getVisibility()==View.VISIBLE){
					MzjLog.i("30秒后转换回开始页面","2");
					changePages();
					//}
					break;

				case 11:
					//3秒后转换右边页面
					if(frameLayout_wheel.getVisibility()==View.VISIBLE && httpSendWeidhtFlag==false){
						changePages();
					}
					break;
				case 12:
					//设备编码设置
					String deviceNum = (String) msg.obj;
//				text_deviceNum.setText(deviceNum);
					break;
				case 13:
					//电子秤端口，即是串口断开
//				linearLayout_network.setVisibility(View.VISIBLE);
//				linearLayout_midller.setVisibility(View.GONE);
//				linearLayout_downPart.setVisibility(View.GONE);
//				frameLayout_staticwheel.setVisibility(View.GONE);
//				if(NetworkUtil.isNetworkConnected(thisContext)){
//					web_pic.setBackground(thisContext.getResources().getDrawable(R.drawable.port_pic));
//					text_networkDown.setText(thisContext.getString(R.string.portAnomaly));
//				}
//				text_weight.setText(getString(R.string.weight));
					break;

				case 14:
//				if(NetworkUtil.isNetworkConnected(thisContext)){
//					linearLayout_network.setVisibility(View.GONE);
//					linearLayout_midller.setVisibility(View.VISIBLE);
//					linearLayout_downPart.setVisibility(View.VISIBLE);
//					frameLayout_staticwheel.setVisibility(View.VISIBLE);
//					text_scan.setText(thisContext.getString(R.string.scan));
//					text_weight.setText(thisContext.getString(R.string.weight));
//				}
//				break;
				case 15:
					linearLayout_network.setVisibility(View.VISIBLE);
					linearLayout_midller.setVisibility(View.GONE);
					frameLayout_wheel.setVisibility(View.GONE);
					frameLayout_staticwheel.setVisibility(View.GONE);
					//20秒后回到开始页面
					MyTimerTask myTimerTask2 = new MyTimerTask(handler, weightDelay, 18, msg.obj);
					myTimerTask2.startTimer();
					break;
				case 18:
					linearLayout_network.setVisibility(View.GONE);
					changePages();
					break;
				case 19:
					//wifi信号
					Integer level = (Integer)msg.obj;
					//根据获得的信号强度发送信息
					int imageType = 1;
					if(level==0){
						signImage.setVisibility(View.GONE);
						break;
					}else if (level < 0 && level >= -50) {
						imageType = R.drawable.wifi_1;
					} else if (level < -50 && level >= -70) {
						imageType = R.drawable.wifi_2;
					} else if (level < -70 && level >= -80) {
						imageType = R.drawable.wifi_3;
					} else if (level < -80 && level >= -100) {
						imageType = R.drawable.wifi_4;
					} else {
						imageType = R.drawable.wifi_5;
					}
					signImage.setVisibility(View.VISIBLE);
					mobileImage.setVisibility(View.GONE);
					signImage.setBackground(thisContext.getResources().getDrawable(imageType));
					break;
				case 20:
					//设置屏幕亮度

					break;
				case 22:
					text_deviceId.setText("设备号："+msg.obj);
					break;
				case 23:
					int index = (int) msg.obj;
						playStandOn(index);
					break;
				case 24:
					HashMap<String,String> map = (HashMap<String, String>) msg.obj;
					String sign = map.get("sign");
					imageType = 0;
					if (sign.equals("0")) {
						imageType = R.drawable.signal_0;
					} else if (sign.equals("1")) {
						imageType = R.drawable.signal_1;
					} else if (sign.equals("2")) {
						imageType = R.drawable.signal_2;
					} else if (sign.equals("3")) {
						imageType = R.drawable.signal_3;
					} else {
						imageType = R.drawable.signal_4;
					}
					signImage.setVisibility(View.GONE);
					mobileImage.setVisibility(View.VISIBLE);
					mobileImage.setBackground(thisContext.getResources().getDrawable(imageType));
					break;
				case 25:
					String hide = (String) msg.obj;
					if(hide!=null){
						if(hide.equals("0")){
							signLayout.setVisibility(View.VISIBLE);
						}else{
							signLayout.setVisibility(View.GONE);
						}
					}
					break;
				case 26:
					JSONObject taskDetail = null;
					String pageType = "";
					String appid = "";
					try {
						taskDetail = new JSONObject(String.valueOf(msg.obj));
						pageType = taskDetail.getString("showWeight");
						if(taskDetail.has("appid")) {
							appid = taskDetail.getString("appid");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(pageType.isEmpty()&&pageType.equals("")){
						DeskActivity.pageType=0;
					}else if(pageType.equals("1")){
						DeskActivity.pageType=1;
					}else if(pageType.equals("2")){
						DeskActivity.pageType=2;
					}else {
						DeskActivity.pageType=0;
					}
					spf.putData("pageType", DeskActivity.pageType);
					if(!appid.isEmpty()&&!appid.equals("")) {
						DeskActivity.appid = appid;
						spf.putData("appid", DeskActivity.appid);
					}
					break;

				case 27:
					setImageRight();
					break;
			}
		}
	};

	private static void setImageRight(){
		hynText.setVisibility(View.GONE);
		frameLayout_qrCodeBig.setVisibility(View.VISIBLE);
		frameLayout_staticwheel.setVisibility(View.VISIBLE);
		ivCodeBg.setVisibility(View.GONE);
		text_scan.setVisibility(View.VISIBLE);
//		text_sao_hint.setVisibility(View.VISIBLE);
		linearLayout_downPart.setVisibility(View.VISIBLE);
		text_scan.setText(R.string.text_deng_standed);
		text_weight.setVisibility(View.GONE);
//		linearLayout_downPart.setBackground(null);
		image_right.setVisibility(View.VISIBLE);
		String filePath = FileUtils.getFileRoot(thisContext) + File.separator+ "qr_code" + ".png";

		setDrawable(image_right, BitmapDrawable.createFromPath(filePath));

	}
	/**
	 * 人员站上去后读出体重
	 * 隐藏二维码，开始播放gif,同时播放语音环节
	 * **/
	private static  void standed(final String weight){
		frameLayout_wheel.setVisibility(View.GONE);
		switch (pageType){
			case 1://直接显示体重
				frameLayout_staticwheel.setVisibility(View.GONE);
				textView_randomCode.setText(weight + "kg");
				linearLayout_midller.setVisibility(View.VISIBLE);
				linearLayout_qrCodeSmall.setVisibility(View.VISIBLE);
				hynText.setText("当前体重");
				break;
			case 2://扫一扫
				final String filePath = FileUtils.getFileRoot(thisContext) + File.separator
						+ "qr_code" + ".png";
				//  https://www.baidu.com/s?wd=android%20生成二维码白边&pn=10
				//二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
				new Thread(new Runnable() {
					@Override
					public void run() {

						String surl = Commons.QRCodeUrl+"machineId="+Commons.deviceId+"&weight="+weight+"&appId="+appid+"&reqTime="+new Date().getTime()+"&channel="+2;
						boolean success = ZXingUtilssss.createQRImage(surl, 250, 250,
								BitmapFactory.decodeResource(thisContext.getResources(), R.drawable.icon_2) ,
								filePath);

						if (success) {
							Message msg = new Message();
							msg.what =27;
							handler.sendMessage(msg);
						}
					}
				}).start();

				new Thread(){
					public void run(){
						try {
							Thread.sleep(1000);		//20170915  1200发现有些设备存在语言播放不出的现象 将时间延长
							musicThread.setFileName(Commons.musicPath+"/music4.mp3");
							musicThread.playMusic();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				break;

			default://关注默认订阅号
				MzjLog.i("随机码设置",""+weight);
				frameLayout_staticwheel.setVisibility(View.GONE);
				textView_randomCode.setText(weight + "");
				linearLayout_midller.setVisibility(View.VISIBLE);
				linearLayout_qrCodeSmall.setVisibility(View.VISIBLE);
				text_scan.setText(thisContext.getString(R.string.authCode));
				text_weight.setText(thisContext.getString(R.string.acceptWeightData));

				text_weight.setVisibility(View.VISIBLE);
				//GrgLog.i("随机码显 示出来","播放提示语音");
				//播放语音环节
				new Thread(){
					public void run(){
						try {
							Thread.sleep(3200);		//20170915  1200发现有些设备存在语言播放不出的现象 将时间延长
							musicThread.setFileName(Commons.musicPath+"/music2.mp3");
							musicThread.playMusic();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}.start();
				break;

		}


		//30秒后回到开始页面
		if(whileTimerTask==null){
			whileTimerTask = new MyTimerTask(handler, weightDelay, 10, weight);
		}
		whileTimerTask.addTimeTask(weightDelay);
	}



	/**
	 * 人员站上去时，未读出体重
	 * **/
	private static void standing(){
		linearLayout_midller.setVisibility(View.VISIBLE);
		frameLayout_wheel.setVisibility(View.VISIBLE);
		frameLayout_staticwheel.setVisibility(View.GONE);
		linearLayout_qrCodeSmall.setVisibility(View.GONE);
		linearLayout_network.setVisibility(View.GONE);
		linearLayout_downPart.setBackgroundResource(R.drawable.word_line);
		switch (pageType){

			case 1://直接显示体重
				hynText.setText(R.string.text_title_later);
				linearLayout_downPart.setVisibility(View.INVISIBLE);
				hynText.setVisibility(View.VISIBLE);

				new Thread(){
					public void run(){
						try {
//							Thread.sleep(500);		//20170915  1200发现有些设备存在语言播放不出的现象 将时间延长
							musicThread.setFileName(Commons.musicPath+"/music3.mp3");
							musicThread.playMusic();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				break;
			case 2://扫一扫

				text_scan.setText(R.string.text_title_ceter);
				text_weight.setVisibility(View.GONE);
				hynText.setText(R.string.text_title_later);

//				changeTestSize(hynText,50,0,5);
//				changeTestSize(hynText,5,hynText.getText().length());
				linearLayout_downPart.setVisibility(View.VISIBLE);
				image_right.setVisibility(View.GONE);
				hynText.setVisibility(View.VISIBLE);

				new Thread(){
					public void run(){
						try {
							musicThread.setFileName(Commons.musicPath+"/music3.mp3");
							musicThread.playMusic();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				break;
			default://关注默认订阅号
				//隐藏二维码，开始播放gif,同时播放语音环节
				linearLayout_downPart.setVisibility(View.VISIBLE);
				text_scan.setText(thisContext.getString(R.string.measure));
				text_weight.setText(thisContext.getString(R.string.balance));

				text_weight.setVisibility(View.VISIBLE);
				//播放语音环节
				musicThread.setFileName(Commons.musicPath+"/music1.mp3");
				musicThread.playMusic();
				break;
		}

	}
	/**
	 * 图片动画效果，暂停要恢复
	 */
	public void resumeRotation(View view) {
		//重新回到主页
		playVideoOrImage();
		gifImageView1.setVisibility(View.VISIBLE);
		gifImageView2.setVisibility(View.VISIBLE);
	}

	private static void changeTestSize(TextView textView,int start,int end){
		Spannable span = new SpannableString(textView.getText());
		span.setSpan(new AbsoluteSizeSpan(58), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(span);
	}
	private static void changeTestSize(TextView textView,int size,int start,int end){
		Spannable span = new SpannableString(textView.getText());
		span.setSpan(new AbsoluteSizeSpan(size), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(span);
	}

//	// 跳转回开始页面
//	public static void setRightPageBacktoBegin() {
//		if(pageType!=0){
//			hynText.setText("欢迎您");
//		}
//		linearLayout_midller.setVisibility(View.VISIBLE);
//		frameLayout_qrCodeBig.setVisibility(View.VISIBLE);
//		frameLayout_staticwheel.setVisibility(View.VISIBLE);
//		frameLayout_wheel.setVisibility(View.GONE);
//		linearLayout_qrCodeSmall.setVisibility(View.GONE);
//		//改变屏幕右下角文字
//		text_scan.setText(thisContext.getString(R.string.scan));
//		text_weight.setText(thisContext.getString(R.string.weight));
//	}


	/**
	 * 暂停播放，回到主页面
	 */
	public static void pausePlay(View view) {

		// 暂停标志
		imageAnimationPlayingStop = true;
		// 如果视频正在播放，那么暂停
		if (playStatus == 1) {
			if (mediaPlayerSv != null) {
				if (mediaPlayerSv.isPlaying() == true) {
					mediaPlayerSv.pause();
					mediaplayerPause = true;
				}
				surfaceViewSv.setAlpha(0);
				surfaceViewSv.setVisibility(View.GONE);
			}
		} else if (playStatus == 2) {
			// 如果图片正在播放，那么暂停
			// 图片1和2隐藏
			imagePausePlay();
			//停止定时器
			advTimerTask.stopTimer();


			// 图片显示已经用了的时间
			imageUseTime = (int)(new Date().getTime() - startTime.getTime());
		}

	}

	public static void imagePausePlay(){
		gifImageView1.setVisibility(View.GONE);
		gifImageView2.setVisibility(View.GONE);
//		gifImageViewStandOn.setVisibility(View.VISIBLE);
	}


	public static void sendMessage(int what) {
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}

	public static void hideImageVideo2(int value1,int value2) {
//		gifImageViewStandOn.setVisibility(value1);
		gifImageView1.setVisibility(value2);
		gifImageView2.setVisibility(value2);
	}


	/**
	 * 隐藏主页和轮播图片页面
	 */

	public static void hideImageVideo() {
//		gifImageViewStandOn.setVisibility(View.GONE);
		gifImageView1.setVisibility(View.GONE);
		gifImageView2.setVisibility(View.GONE);
	}

	/**
	 * 设置gif或者是图片
	 * @param fileName
	 * @param gifImageView
	 */
	public static void setGifImageView(String fileName,GifImageView gifImageView){
		File file = new File(fileName);
		try {
			String name = file.getName();
			int length = file.getName().length();
			if(name.substring(length-4,length).equals(".gif")){
				GifDrawable gifDrawable = new GifDrawable(fileName);
				gifImageView.setImageDrawable(gifDrawable);
			}else{
				bitmap = BitmapFactory.decodeFile(fileName);
				if(bitmap==null){
					gifImageView.setImageResource(R.drawable.mzj);
				}else {
					gifImageView.setImageBitmap(bitmap);
				}
				gifImageView.setScaleType(ImageView.ScaleType.FIT_XY);
			}
		} catch (Exception e) {
			gifImageView.setImageResource(R.drawable.mzj);
			e.printStackTrace();
		}
	}


	/**
	 * 上送设备状态。即是上送心跳包
	 *
	 * @param view
	 */
	public void getDeviceStatusAndControlApp(View view) {
		HttpGetData httpGetData = new HttpGetData(this,handler);
		httpGetData.getDeviceStatusAndControlApp();
	}


	/*****
	 *
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {

				String tempLongitude=""+location.getLongitude();//经度
				String tempLatitude=""+location.getLatitude();//维度
				if((tempLongitude.equalsIgnoreCase("4.9e-324")||tempLongitude.equalsIgnoreCase("5e-324"))&&("".equals(longitude))){
					longitude="";
				}else{
					longitude=tempLongitude;//经度
				}
				if((tempLatitude.equalsIgnoreCase("4.9e-324")||tempLatitude.equalsIgnoreCase("5e-324"))&&("".equals(latitude))){
					latitude="";
				}else{
					latitude=tempLatitude;//经度
				}
				//	GrgLog.i("latitude1", latitude);
				//	GrgLog.i("latitude2", longitude);
			}
		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	   if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//		    finish();
//	        System.exit(0);
//            return true;
//       }
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onStop() {
		super.onStop();
	}


	@Override
	protected void onDestroy() {
		if(sp!=null){
			sp.close();
		}

		if(locationService!=null){
			locationService.unregisterListener(mListener); //注销掉监听
			locationService.stop(); //停止定位服务
			locationService = null;
		}

		super.onDestroy();
	}

}
