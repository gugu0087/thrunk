package com.mzj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BatteryListener {

    private Context mContext;

    private BatteryBroadcastReceiver receiver;

    private BatteryStateListener mBatteryStateListener;

    public BatteryListener(Context context) {
        mContext = context;
        receiver = new BatteryBroadcastReceiver();
    }

    public void register(BatteryStateListener listener) {
        mBatteryStateListener = listener;
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Intent.ACTION_BATTERY_LOW);
            filter.addAction(Intent.ACTION_BATTERY_OKAY);
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String acyion = intent.getAction();
                switch (acyion) {
                    case Intent.ACTION_BATTERY_CHANGED://电量发生改变
                        if (mBatteryStateListener != null) {
                            Log.e("zhang", "BatteryBroadcastReceiver --> onReceive--> ACTION_BATTERY_CHANGED");
                            mBatteryStateListener.onStateChanged();
                        }
                        break;
                    case Intent.ACTION_BATTERY_LOW://电量低
                        if (mBatteryStateListener != null) {
                            Log.e("zhang", "BatteryBroadcastReceiver --> onReceive--> ACTION_BATTERY_LOW");
                            mBatteryStateListener.onStateLow();
                        }
                        break;
                    case Intent.ACTION_BATTERY_OKAY://电量充满
                        if (mBatteryStateListener != null) {
                            Log.e("zhang", "BatteryBroadcastReceiver --> onReceive--> ACTION_BATTERY_OKAY");
                            mBatteryStateListener.onStateOkay();
                        }
                        break;
                    case Intent.ACTION_POWER_CONNECTED://接通电源
                        if (mBatteryStateListener != null) {
                            Log.e("zhang", "BatteryBroadcastReceiver --> onReceive--> ACTION_POWER_CONNECTED");
                            mBatteryStateListener.onStatePowerConnected();
                        }
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED://拔出电源
                        if (mBatteryStateListener != null) {
                            Log.e("zhang", "BatteryBroadcastReceiver --> onReceive--> ACTION_POWER_DISCONNECTED");
                            mBatteryStateListener.onStatePowerDisconnected();
                        }
                        break;
                }
            }
        }
    }

    public interface BatteryStateListener {
        void onStateChanged();

        void onStateLow();

        void onStateOkay();

        void onStatePowerConnected();

        void onStatePowerDisconnected();
    }

}