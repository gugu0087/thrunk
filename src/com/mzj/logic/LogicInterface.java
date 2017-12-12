package com.mzj.logic;

import android.content.Context;
import android.os.Handler;

/**
 * Created by hasee on 2017/12/6.
 *
 */

public interface LogicInterface {

    void deleteAdOrImg();

    void getMainThread(Runnable runnable);

    Context getApplicationContext();

    void deleteAdById(String id);

    void getReloadAdList();

    Handler getBackgroundHandler();

}
