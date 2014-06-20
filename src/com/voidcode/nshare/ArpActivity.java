package com.voidcode.nshare;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.voidcode.nshare.R;

public class ArpActivity extends Activity {
	private TextView tvArptabel;
	private ARPtabel ARPtabel = new ARPtabel();
	private ActionBar actionbar;
	private Typeface uFont;
	private ArrayList<String> macArray = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedIntentceState)
	{
		super.onCreate(savedIntentceState);
		setContentView(R.layout.activity_arp);
		uFont = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");
		
		setupActionBar();
		
		tvArptabel = (TextView)findViewById(R.id.tvArptabel);
		
		//macArray  = ARPtabel.getMacArrayList();
		tvArptabel.setText(ARPtabel.get());
		tvArptabel.setMovementMethod(new ScrollingMovementMethod());
	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void setupActionBar()
	{
		actionbar = (ActionBar) getActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setSubtitle("/proc/net/arp");
		TextView subbarTitle = (TextView) findViewById(getResources().getIdentifier("action_bar_subtitle", "id", "android"));
		subbarTitle.setTypeface(uFont);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		 	case android.R.id.home:
		 		finish();
	            return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
