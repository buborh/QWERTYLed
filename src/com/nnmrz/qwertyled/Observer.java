package com.nnmrz.qwertyled;

import java.io.IOException;

import android.os.FileObserver;
import android.util.Log;

public class Observer extends FileObserver{

	public Observer(String path) {
		super(path);
		Log.d(QWERTYLed.tag, "in observer");
	}

	@Override
	public void onEvent(int event, String path) {
		if(event != FileObserver.MODIFY) {
			Log.d(QWERTYLed.tag, "no modify");
			return;
		}
		Log.d(QWERTYLed.tag, "onEvent");
		try {
			LedControl.ALS_STATE = LedControl.getBrightness();
		} catch (IOException e) {
			e.printStackTrace();
			}
		Log.d(QWERTYLed.tag, "ALS_STATE = " + LedControl.ALS_STATE);
		if(LedControl.ALS_STATE < LedControl.sensorthreshold) {
			if(LedControl.LED_STATE != LedControl.LED_ON)
			{
				Log.d(QWERTYLed.tag, "LED_ON ");
				 try {
					 LedControl.setKeyLight(LedControl.LED_ON);
				} catch (IOException e) {
					e.printStackTrace();
					}
			}
		}
		else {
			if(LedControl.LED_STATE != LedControl.LED_OFF)
			{
				Log.d(QWERTYLed.tag, "LED_OFF ");
				try {
					LedControl.setKeyLight(LedControl.LED_OFF);
				} catch (IOException e) {
					e.printStackTrace();
					}
			}
		}
		
	}

}
