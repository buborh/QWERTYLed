package com.nnmrz.qwertyled;


import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.File;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;
import android.content.SharedPreferences;
import android.app.Activity;
import android.preference.PreferenceManager;

public abstract class LedControl extends Activity  {
	final static int LED_OFF = 0;
	final static int HARDKEYBOARD_YES = 1;
	final static int HARDKEYBOARD_NO = 2;
	static int HARDKEYBOARD_STATE = HARDKEYBOARD_NO;
	static int LED_STATE = LED_OFF;
	static int ALS_STATE = -1;
	static int alsLevel;
    SensorManager sm = null;
	static InputStream is;
	static InputStreamReader ir;
	Sensor lightSensor;
	static Thread chLight;
	static Thread SendDataThread = null;
	static boolean thrStop = false;
    static String sensorfile ;
    private static String keyfile ;
    static int sensorthreshold = 20;
    static int LED_ON ;
    int siz;
    public static boolean fileExists( String val) {
    	File file = new File(val);
    	return file.exists();	
    }
    public static int getBrightness() throws IOException {
      return (int) getValue(sensorfile);
    }  
    public static float getValue(String val) throws IOException {
		float sensor = -1;
		if (!fileExists(sensorfile))
			return 0;
		
    	is = new FileInputStream(sensorfile);
	    ir = new InputStreamReader(is);
	    
	    char[] buf = new char[20];
	    int siz = ir.read(buf, 0, 20);
	    if (siz > 0)
	    {
	    	sensor = Float.parseFloat(String.copyValueOf(buf, 0, siz).replaceAll(",", "."));
	    }
	    is.close();
	    ir.close();
		return sensor;
    	
    }
    
    public static boolean setKeyLight(int val) throws IOException {
		if (!fileExists(keyfile))
			return true;
    	Log.d(QWERTYLed.tag, "set keylight to value:" + val);
    	FileOutputStream localFileOutputStream = new FileOutputStream(keyfile);
        byte[] arrayOfByte = (val + "\n").getBytes("ASCII");
        localFileOutputStream.write(arrayOfByte);
        localFileOutputStream.close();
        LED_STATE = val;
		return true;
    }
    public static boolean init(Context paramContext) throws IOException
    {
    	int uid = paramContext.getApplicationContext().getApplicationInfo().uid;
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(paramContext);
        keyfile = sharedPrefs.getString("keyfile", "/sys/class/sec/sec_stmpe_bl/backlight");
        LED_ON = Integer.valueOf(sharedPrefs.getString("LED_ON", "1")) ;
        sensorfile = sharedPrefs.getString("sensor", "/sys/devices/virtual/lightsensor/switch_cmd/lightsensor_file_state");
        sensorthreshold= Integer.valueOf(sharedPrefs.getString("sensorthreshold", "30"));
        Process lp = Runtime.getRuntime().exec("su");
        OutputStream os = lp.getOutputStream();
        DataOutputStream dataos = new DataOutputStream(os);
        String str = "chown " + uid + " " + keyfile + "\n";
        dataos.writeBytes(str);
        dataos.writeBytes("chmod u+rw BRIGHTNESS_FILE\n");
        dataos.writeBytes("exit\n");
        dataos.flush();
        dataos.close();
        os.close();
		return true;
    }
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d(QWERTYLed.tag, "Sendor accuracy changed:" + sensor.getName() + ": " + accuracy);
	}

	public void onSensorChanged(SensorEvent event) {
		Log.d(QWERTYLed.tag, "Sensor value changed:" + event.values[0]);
		
	}
	
	public static void qwertyStatSwitcher(int stat) {
		 switch (stat)
		 {
		 case HARDKEYBOARD_NO:
			 thrStop = true;
			 if(HARDKEYBOARD_STATE != HARDKEYBOARD_NO) {
				 Log.d(QWERTYLed.tag, "Keyboard NO");
				 try {
					 setKeyLight(LED_OFF);
					 } catch (IOException e1) {
						 Log.d(QWERTYLed.tag, e1.toString());
						 }
			 }
			 break;
		 case HARDKEYBOARD_YES:
			 thrStop = false;
			 Log.d(QWERTYLed.tag, "HARDKEYBOARD_YES");
			 getBrightnessCycle();
			 break;
			 }
		 }
	public static int getBrightnessCycle() {
		Log.d(QWERTYLed.tag, "getBrightnessCycle started");	
		if ( SendDataThread == null ) {
			Log.d(QWERTYLed.tag, "thread started");
			SendDataThread = new Thread(run);
			SendDataThread.start();
		} else {
			Log.d(QWERTYLed.tag, "thread already started");			
		}
		return ALS_STATE;
	}
	public static void Stop() {
		Log.d(QWERTYLed.tag, "stop()");
		thrStop = true;
		SendDataThread = null;
 	}
	public void onDestroy() {
	    Log.d(QWERTYLed.tag, "onDestroy LedControl");
		super.onDestroy();
	}
	static Runnable run = new Runnable()  {
		public void run() {
			while(!thrStop )
			{
				ALS_STATE = -1;
				try {
					ALS_STATE = getBrightness();
				} catch (IOException e) {
					e.printStackTrace();
					}
				Log.d(QWERTYLed.tag, "ALS_STATE = " + ALS_STATE);
				if(ALS_STATE < sensorthreshold && ALS_STATE >= 0) {
					if(LED_STATE != LED_ON)
					{
						Log.d(QWERTYLed.tag, "LED_ON ");
						 try {
							setKeyLight(LED_ON);
						} catch (IOException e) {
							e.printStackTrace();
							}
						 // prevent to turn off right after
						 SystemClock.sleep(5000);
					}
				}
				else {
					if(ALS_STATE > (sensorthreshold*1.2) && LED_STATE != LED_OFF)
					{
						Log.d(QWERTYLed.tag, "LED_OFF ");
						try {
							setKeyLight(LED_OFF);
						} catch (IOException e) {
							e.printStackTrace();
							}
						 // prevent to turn on right after
						 SystemClock.sleep(2000);
					}
				}
				SystemClock.sleep(500);
			}
			Log.d(QWERTYLed.tag, "thread end");
			thrStop = false;
			SendDataThread = null;
		}
	};	
}
