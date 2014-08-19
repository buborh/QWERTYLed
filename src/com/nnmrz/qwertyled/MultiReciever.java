package com.nnmrz.qwertyled;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MultiReciever extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(QWERTYLed.tag, "onReceive " + intent.getAction());
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			context.startService(new Intent(context, LedControlService.class));
		
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
				LedControl.qwertyStatSwitcher(LedControl.HARDKEYBOARD_NO);
		
		if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
			LedControl.qwertyStatSwitcher(LedControl.HARDKEYBOARD_STATE);
	}
}
