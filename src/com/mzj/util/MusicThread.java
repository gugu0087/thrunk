package com.mzj.util;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.PowerManager;

import com.mzj.scale_app.DeskActivity;


/**
 * 音乐播放服务类
 * @author fyf000
 *
 */
public class MusicThread extends Thread {


	private Activity activity;
	private String fileName;
	public static float volume=1;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	MediaPlayer mp = new MediaPlayer();


	public MusicThread(Activity activity){
		this.activity = activity;
	}



	public void run(){
		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				DeskActivity.setVolumeUp();

				mp.reset();
			}

		});
	}


	//播放音乐
	public void playMusic() {
		final PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
		if(mp.isPlaying() == false && powerManager.isScreenOn()== true){
			try {
				mp.setDataSource(fileName);
				prepare();
				DeskActivity.setVolumeDown();
				mp.start();
				mp.setVolume(volume, volume);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//暂停音乐
	public void pauseMusic() {
		mp.pause();
	}

	//停止音乐
	public void stopMusic() {
		mp.stop();
	}

	//重新播放
	public void rePlayMusic() {
		mp.reset();
		prepare();
		this.playMusic();
	}


	//音乐初始化
	public void prepare() {
		try {
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVolumeDown(){
		mp.setVolume(0, 0);
	}


	public void setVolumeUp(){
		//mp.setVolume(1, 1);
	}



}