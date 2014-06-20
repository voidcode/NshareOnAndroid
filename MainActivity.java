package com.voidcode.nshare;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.voidcode.nshare.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends Activity  {
	private Typeface uFont;
	private Bundle extras;
	private ArrayList<Device> deviceItems = new ArrayList<Device>();
	private String extraText;
	private ListView lvMainDevices;
	private TextView tvMainUrl;
	private ActionBar actionbar;
	private String TAG = "MainActivity";
	private Builder alertDialogAddUrl;
	private int numOfSelectDeviceItems = 0;
	private TextView tvMainNumOfDeviceSelect;
	private Typeface uFontBold;
	public EditText etUrl;
	private ClipboardManager clipboard;
	protected ArrayList<String> stackOfToIPs = new ArrayList<String>();
	private DeviceAdapter da;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//start NotificationService if none-running!
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (!"com.voidcode.nshare.NotificationService".equals(service.service.getClassName())) {
	        	startService(new Intent(this, NotificationService.class));
	        }
	    }
		
		//get ubuntu-font
		uFontBold = Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-B.ttf");
		uFont = Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf");

		//setup actionbar
		actionbar = (ActionBar) getActionBar();
		actionbar.setHomeButtonEnabled(true);
		
		TextView action_bar_title = (TextView) findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
		action_bar_title.setTypeface(uFont);
		
		//fill listview	
		lvMainDevices = (ListView) findViewById(R.id.lvMainDevices);		
		
		//fill incomming url
		tvMainUrl = (TextView)findViewById(R.id.tvMainUrl);
		extras = getIntent().getExtras();
		if(extras != null && extras.get(Intent.EXTRA_TEXT) !=null)
		{			
			extraText = (String) extras.get(Intent.EXTRA_TEXT);//gets.. url
			if(extraText !=null)
				tvMainUrl.setText(extraText);
		}
		else
		{
			etUrl = new EditText(this);
			etUrl.setTypeface(uFont);
			InputMethodManager imm = (InputMethodManager)getSystemService(
			Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etUrl.getWindowToken(), 0);
			AlertDialogAddUrl(this, "Add url or sometext");
		}
		da = new DeviceAdapter(this, R.layout.deviceadapter_item, deviceItems, uFont);
		lvMainDevices.setAdapter(da);
		lvMainDeviecOnclickAndLongClickListenter();
		new UpdateDeviceItemsTask(this, deviceItems, getWifiIP().split("\\."), uFont).execute(true);//<-- find any other device on current network-segment

		//Num of DeviceSelect 
		tvMainNumOfDeviceSelect = (TextView)findViewById(R.id.tvMainNumOfDeviceSelect);
		tvMainNumOfDeviceSelect.setTypeface(uFontBold);
	}
	public void ibMainSendPacketClicked(View v)
	{
		if(numOfSelectDeviceItems >0)
			sendStackOfIPsToTask();
		else
			Toast.makeText(v.getContext(), "Please select! one or more devices ...", Toast.LENGTH_SHORT).show();
	}
	private void unSelectLvMainDevicesItems()
	{
		//reset user arguments
		for(int i=0; i<lvMainDevices.getCount(); i++)//<-- set all deviceItems to default-state
			lvMainDevices.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
		tvMainNumOfDeviceSelect.setText("(0 select)");
		numOfSelectDeviceItems=0;
		stackOfToIPs = new ArrayList<String>();
	}
	private void sendStackOfIPsToTask()
	{
		Log.d(TAG, "extraText->"+extraText+", stackOfToIP->"+stackOfToIPs.toString());

		if(extraText !=null && stackOfToIPs !=null)
		{
			try {
				AsyncTask<Void, Integer, Boolean> task = new SendPacketStackTask(extraText, stackOfToIPs).execute();
				if(task.get())//<-- return 'true' if no-error is detected
				{ 
					unSelectLvMainDevicesItems();
					Toast.makeText(this, "Packets is sendt!", Toast.LENGTH_SHORT).show();
					finish();
				}
				else
					Toast.makeText(this, "Ups: an error is detected!", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void fillClipboardIntoEtUrl()
	{
		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
		String clipboardText = clipboard.getText().toString();
		etUrl.setText(clipboardText);
		etUrl.setSelection(clipboardText.length());//<-- reset curser
	}
	public void AlertDialogAddUrl(Context c, String title)
	{
		alertDialogAddUrl = new AlertDialog.Builder(c);
		if(title !=null)
			alertDialogAddUrl.setTitle(title);
		alertDialogAddUrl
		.setView(etUrl)
        .setPositiveButton("OK, use this data", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(etUrl !=null)
				{
					if(etUrl.getText().length() >0)
					{
						tvMainUrl.setText(etUrl.getText().toString());
						extraText = etUrl.getText().toString();
						dialog.dismiss();
					}
				}
			}
		}).setCancelable(false).show();
		fillClipboardIntoEtUrl();
	}
	public void lvMainDeviecOnclickAndLongClickListenter()
	{
		//on shot click set bckgroud as green
		lvMainDevices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parnet, View v, int pos, long id) {
				lvMainDevices.getChildAt(pos).setBackgroundColor(Color.parseColor("#5AA05A"));
				if((numOfSelectDeviceItems < lvMainDevices.getCount()) && !stackOfToIPs.contains(deviceItems.get(pos).getIp()))
				{
					stackOfToIPs.add(deviceItems.get(pos).getIp());
					numOfSelectDeviceItems++;
					tvMainNumOfDeviceSelect.setText("("+numOfSelectDeviceItems+" select)");
				}
			}
		});
		//on long click. Remove color
		lvMainDevices.setLongClickable(true);
		lvMainDevices.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parnet, View v, int pos, long id) {
				lvMainDevices.getChildAt(pos).setBackgroundColor(Color.TRANSPARENT);
				if((numOfSelectDeviceItems > 0) && stackOfToIPs.contains(deviceItems.get(pos).getIp()))
				{
					stackOfToIPs.remove(deviceItems.get(pos).getIp());
					numOfSelectDeviceItems--;
					tvMainNumOfDeviceSelect.setText("("+numOfSelectDeviceItems+" select)");
				}
				return true;
			}
		});
	}
	private String getWifiIP()
	{
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		return Formatter.formatIpAddress(ip);
	}
	private void scanNetworkSegment()
	{
		unSelectLvMainDevicesItems();
		new UpdateDeviceItemsTask(this, deviceItems, getWifiIP().split("\\."), uFont).execute(true);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}	
	/*CODE BELOW IS FOR THE MENU*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		 	case android.R.id.home:
		 		new AlertDialog.Builder(this)
	            .setTitle("Kill "+getString(R.string.app_name)+" now?")
	            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	finish();
	                }
	            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    dialog.dismiss();
	                }
	            }).setCancelable(false).show();
	            return true;
			case R.id.action_scan_networksegment:
				scanNetworkSegment();
				return true;
			case R.id.action_viewarptabel:
				startActivity(new Intent(this, ArpActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();  
		//stopService(new Intent(this, NotificationService.class));
	}
}
