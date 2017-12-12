package com.mzj.scale_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import com.grg.weight.R;
import com.mzj.database.LogicDbManager;
import com.mzj.service.LocationService;
import com.mzj.util.MzjLog;

public class MainActivity extends Activity {


//	public static String oldAdFileName = "";

//	public static Activity thisContent;

	public LocationService locationService;
	public Vibrator mVibrator;

	TelephonyManager telephoneManager;
	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		thisContent = this;
		//开始处理逻辑代码
		MainActivityHelper m = new MainActivityHelper(this);
		m.init();

	}

	@Override
	protected void onResume() {
		super.onResume();
		//MzjLog.i("gotoDeskActivity", "gotoDeskActivity");
		gotoDeskActivity();//跳转到主页
	}

	/**
	 * 跳转到广告主页面
	 */
	public void gotoDeskActivity() {
		MzjLog.i("gotoDeskActivity1", "gotoDeskActivity1");
		Intent intentDeskActivity = new Intent(MainActivity.this, DeskActivity.class);
		startActivity(intentDeskActivity);
		MzjLog.i("gotoDeskActivity2", "gotoDeskActivity2");
		this.finish();
	}


	@Override
	protected void onStart() {
		super.onStart();

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
