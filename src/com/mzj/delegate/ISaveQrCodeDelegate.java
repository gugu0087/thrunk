package com.mzj.delegate;

import android.content.Context;

/**
 * Created by hasee on 2017/11/30.
 */

public interface ISaveQrCodeDelegate {
    void  saveFinish(boolean isFinish, String taskId, String taskType);
    void saveFailed(String msg, String taskId,String taskType);
}
