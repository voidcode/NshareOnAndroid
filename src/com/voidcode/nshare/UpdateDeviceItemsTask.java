package com.voidcode.nshare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

public class UpdateDeviceItemsTask extends AsyncTask<Boolean, Integer, Void> {
	private String TAG = "";
	private ARPtabel arpTabel = new ARPtabel();
	private Pattern isRouterIP = Pattern.compile("192.168.1.1", Pattern.DOTALL);
	private ArrayList<String> ipsInArpTabel;
	private int timeout=3000;
	private Activity activity;
	private ArrayList<Device> deviceItems;
	private Typeface tf;
	private String[] splitIp;
	private String segmentIP;
	private static Pattern hasFlag0x2AndMacIsNotNull = Pattern.compile("([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}).{1,}0x2.{1,}([0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2})");
	private static Matcher m;
	private ArrayList<String> MACs = new ArrayList<String>();
	private ArrayList<String> IPs = new ArrayList<String>();
	private FileInputStream is;
	private File arpFile;
	private BufferedReader reader;
	private String line;
	public UpdateDeviceItemsTask(Activity activity, ArrayList<Device> deviceItems, String[] splitIp, Typeface tf) 
	{
		this.activity = activity;
		this.deviceItems = deviceItems;
		this.splitIp = splitIp;
		this.tf = tf;

		this.segmentIP = splitIp[0]+"."+splitIp[1]+"."+splitIp[2]+".";
	}
	@Override 
	protected void onPreExecute() {
		super.onPreExecute();
		try {
			is = new FileInputStream(new File("/proc/net/arp"));
			reader = new BufferedReader(new InputStreamReader(is));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected Void doInBackground(Boolean... flags) {
		try {
			if(flags[0])
			{
				for (int i=2; i<254; i++)
					InetAddress.getByName(segmentIP+i).isReachable(1);
				deviceItems.clear();
			}
			deviceItems.add(new Device("Your device", segmentIP+splitIp[3]));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				while((line = reader.readLine()) != null)
				{
					m = hasFlag0x2AndMacIsNotNull.matcher(line);
					if(m.find())
					{
						if(!(segmentIP+"1").equals(m.group(1)) )
							deviceItems.add(new Device("Share with", m.group(1)));
					}
				}
				reader.close();
				is.close();
			} 
			catch (FileNotFoundException e) {} 
			catch (IOException e) {}
			finally
			{
				activity.runOnUiThread(new Runnable(){
			        public void run(){
			            final ListView lv = (ListView) activity.findViewById(R.id.lvMainDevices);
			            lv.setAdapter(new DeviceAdapter(activity, R.layout.deviceadapter_item, deviceItems, tf));
			        }
			    });
			}
			
		}
		return null;
	}
}
