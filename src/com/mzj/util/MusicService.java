package com.mzj.util;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;


/**
 * 音乐播放服务类
 * @author fyf000
 *
 */
public class MusicService extends Service {


	//音乐播放文件名 包括路径的
	private String fileName;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	MediaPlayer mp = new MediaPlayer();
	public MyBinder myBinder = new MyBinder();

	@Override
	public IBinder onBind(Intent intent) {

		fileName = intent.getStringExtra("fileName");


		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("musicBroadcast");
				serviceIntent.putExtra("status", "播放结束");
				sendBroadcast(serviceIntent);
			}

		});

		return myBinder;
	}

	public class MyBinder extends Binder {
		public MusicService getMyservice() {
			return MusicService.this;
		}
	}


	//播放音乐
	public void play() {
		mp.start();
	}

	//暂停音乐
	public void pause() {
		mp.pause();
	}

	//停止音乐
	public void stop() {
		mp.stop();
	}

	//重新播放
	public void rePlay() {
		mp.reset();
		prepare(fileName);
		this.play();
	}


	//音乐初始化
	public void prepare(String fileName) {
		try {
			mp.setDataSource(fileName);
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}