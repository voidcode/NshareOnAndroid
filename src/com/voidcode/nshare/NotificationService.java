package com.voidcode.nshare;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.voidcode.nshare.R;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NotificationService extends Service {
	private String TAG = "NotificationService";
	private NotificationManager mNotificationManager;
	private Bundle extras;
	private Bundle bundle;
	private static TreeMap<String, Long> spamList = new TreeMap<String, Long>();
	private static long spamFilterBlockTime = 15000L;
	public NotificationService() {
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	@Override
    public void onCreate() {
       // Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
    }
	@Override
    public void onStart(final Intent intent, int startId) {
    	// For time consuming an long tasks you can launch a new thread here...
    	Thread th = new Thread(new Runnable() {
			private String packetIp;
			private String packetData;
			@SuppressWarnings("resource")
			@Override
			public void run() {
				Log.d(TAG, "onStart()->Thread()");
		    	DatagramSocket ds;
					try {
						ds = new DatagramSocket(Integer.valueOf(getString(R.string.default_port)));
						while(true)
						{
							byte[] buffer = new byte[512];
							DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
							try {
								ds.receive(dp);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Log.d(TAG, new String(dp.getData()).trim());
							packetIp = dp.getAddress().toString();
							packetData = new String(dp.getData()).trim();
							Long now = System.currentTimeMillis();
							for(Map.Entry<String, Long> entry :spamList.entrySet())
							{
								//this allow frist packet to receivar then, 
								//on spam, only remove 'ip' from 'spamList' after 15sec
								if(now > (entry.getValue() + spamFilterBlockTime)) 
									spamList.remove( entry.getKey() );
							}
							if(!spamList.containsKey(packetIp))
							{
								spamList.put(packetIp, System.currentTimeMillis());
								createNotification(intent, packetData, packetIp.substring(1));
							}
						}
					} catch (SocketException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		});
    	th.start();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void createNotification(Intent i, String text, String ip)
    {
    	Intent ii = new Intent(this, CopyActivity.class);
    	ii.putExtra("ip", ip);
    	ii.putExtra("text", text);
    	PendingIntent pIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_CANCEL_CURRENT);
    	// build notification
    	Notification n  = new Notification.Builder(this)
    	        .setContentTitle(text)
    	        .setContentText(ip)
    	        .setSmallIcon(R.drawable.packetbox_small)
    	        .setContentIntent(pIntent)
    	        .setDefaults(Notification.DEFAULT_SOUND)
    	        .setAutoCancel(true).build();
    	NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	notificationManager.notify(0, n); 
    }
    @Override
    public void onDestroy() {
       // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
