package com.nnmrz.qwertyled;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener{

	ImageButton vkBtn, skypeBtn, icqBtn, twitterBtn, gmailBtn, gplusBtn, xdaBtn;
	TextView verText;
	Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        vkBtn = (ImageButton) findViewById(R.id.vk_button);
        skypeBtn = (ImageButton) findViewById(R.id.skype_button);
        icqBtn = (ImageButton) findViewById(R.id.icq_button);
        twitterBtn = (ImageButton) findViewById(R.id.twitter_button);
        gmailBtn = (ImageButton) findViewById(R.id.gmail_button);
        gplusBtn = (ImageButton) findViewById(R.id.gplus_button);
        xdaBtn = (ImageButton) findViewById(R.id.xda_button);
        verText = (TextView) findViewById(R.id.version_num);
        vkBtn.setOnClickListener(this);
        skypeBtn.setOnClickListener(this);
        icqBtn.setOnClickListener(this);
        twitterBtn.setOnClickListener(this);
        gmailBtn.setOnClickListener(this);
        gplusBtn.setOnClickListener(this);
        xdaBtn.setOnClickListener(this);
        try {
			verText.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    }

	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.vk_button:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.vk_url)));
		    startActivity(intent);
			break;
		case R.id.icq_button:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.icq_url)));
		    startActivity(intent);
			break;
		case R.id.twitter_button:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitter_url)));
		    startActivity(intent);
			break;
		case R.id.gmail_button:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gmail_url)));
		    startActivity(intent);
			break;
		case R.id.gplus_button:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gplus_url)));
		    startActivity(intent);
			break;
		case R.id.skype_button:
			SkypeURI.initiateSkypeUri((Context) this, getString(R.string.skype_url));
			break;
		case R.id.xda_button:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.xda_url)));
		    startActivity(intent);
			break;
		}
	}
}

