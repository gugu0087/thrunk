package com.mzj.util;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;

public class AudioManagerUtil {

    private Activity activity;

    private static int currVolume = 0;
    public AudioManagerUtil(Activity activity){
        this.activity = activity;
    }



    //打开扬声器
    public void OpenSpeaker() {
        try{

//判断扬声器是否在打开
            AudioManager audioManager =(AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

            audioManager.setMode(AudioManager.ROUTE_SPEAKER);

//获取当前通话音量
            currVolume =audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if(!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);

                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //关闭扬声器
    public void CloseSpeaker() {

        try {
            AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,0,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(context,扬声器已经关闭",Toast.LENGTH_SHORT).show();
    }
}
