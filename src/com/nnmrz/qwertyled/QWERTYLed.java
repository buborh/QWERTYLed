package com.nnmrz.qwertyled;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import android.view.MenuItem;
import android.widget.TextView;

public class QWERTYLed extends Activity implements OnClickListener{
    final static String tag = "AKL LOGS";
	static InputStream is;
	public Intent serviceIntent ;
	private static final int RESULT_SETTINGS = 1;
    static String sensorfile ;
    static int sensorthreshold = 20;
	static InputStreamReader ir;
	TextView myTextView;
	private Handler mHandler;
	Sensor lightSensor;
    static int LED_ON ;
    static Thread SendDataThread = null;
    Button startBtn, stopBtn, aboutBtn;
    ToggleButton tBtn;
    int siz;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		serviceIntent = new Intent(this, LedControlService.class) ;
		Log.d(tag,"onCreateActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = (Button) findViewById(R.id.start_button);
        stopBtn = (Button) findViewById(R.id.stop_button);
        aboutBtn = (Button) findViewById(R.id.about_button);
        //tBtn = (ToggleButton) findViewById(R.id.toggleButton);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sensorfile = sharedPrefs.getString("sensor", "/sys/devices/virtual/lightsensor/switch_cmd/lightsensor_file_state");
        myTextView = (TextView) findViewById(R.id.currentLight);
        mHandler = new Handler();
        mHandler.post(mUpdate);
    }
    @Override
    public void onDestroy() {
    	Log.d(tag,"onDestroyActivity");
    	super.onDestroy();
    }
    
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
    private Runnable mUpdate = new Runnable() {
    	   public void run() {
    	    	try {
    	    		if ( myTextView != null  )
    	    				myTextView.setText("Current light value:" + getBrightness());
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    				mHandler.postDelayed(this, 1000);
    	    }
  };
    
    public void UpdateText() {
    	TextView myTextView = (TextView) findViewById(R.id.currentLight);
    	try {
    		myTextView.setText("Current light value:" + getBrightness() );
		} catch (IOException e) {
			e.printStackTrace();
			}

    }
	public void onClick(View v) {
		
		switch(v.getId())
		{
		/*case R.id.toggleButton:
			if (tBtn.isChecked())
				stopService(new Intent(this, LedControlService.class));
			else
				startService(new Intent(this, LedControlService.class));*/
		case R.id.start_button:
			startService(serviceIntent);
			break;
		case R.id.stop_button:			
			stopService(serviceIntent);
			break;
		case R.id.about_button:
		    startActivity(new Intent(this, AboutActivity.class));
			break;
		}
	}

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	      
	        case R.id.action_Preferences:
	        	Log.d(tag,"onCreateA");
	            Intent i = new Intent(this, UserSettingActivity.class);
	            startActivityForResult(i, RESULT_SETTINGS);
	            break;
	 
	        }
	 
	        return true;
	    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
}