package com.nnmrz.qwertyled;

import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LedControlService extends Service {
	IntentFilter filter;
	static MultiReciever mr;
	Intent service;
	
	public void onCreate() {
		super.onCreate();
	    filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mr = new MultiReciever();
		registerReceiver(mr, filter);
	    Log.d(QWERTYLed.tag, "onCreate");
	    }
	public void onDestroy() {
		stopForeground(true);
		unregisterReceiver(mr);
	    Log.d(QWERTYLed.tag, "onDestroy");
	    LedControl.Stop();
		super.onDestroy();
	    }
	  
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Log.d(QWERTYLed.tag, "onStartCommand");
	     try {
			LedControl.init(this);
			Log.d(QWERTYLed.tag, "init");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	    LedControl.qwertyStatSwitcher(getResources().getConfiguration().hardKeyboardHidden);
	    
	    service = new Intent(this, LedControlService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, service, 0);
/*	    Notification notif = new NotificationCompat.Builder(getBaseContext())
				.setOngoing(true)
				.setPriority(Notification.PRIORITY_MAX)
				.setContentText(getString(R.string.qwertyled_service_started))
				.setContentTitle(getString(R.string.app_name))
				.setSmallIcon(R.drawable.ic_stat_notify)
				.setContentIntent(pi)
				.build();
		startForeground(1, notif); */
	    return Service.START_STICKY;
	   
	  }
	

	 public IBinder onBind(Intent intent) {
		 Log.d(QWERTYLed.tag, "onBind");
		 return null;
	 }
	 public void onConfigurationChanged(Configuration paramConfiguration) {
			 LedControl.qwertyStatSwitcher(paramConfiguration.hardKeyboardHidden);
			 LedControl.HARDKEYBOARD_STATE = paramConfiguration.hardKeyboardHidden;
		 }
}