package com.voidcode.nshare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.*;

import android.util.Log;

public class ARPtabel {
	private FileInputStream is;
	private static String TAG = "ARPtabel";
	private static Pattern hasFlag0x2AndMacIsNotNull = Pattern.compile("([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}).{1,}0x2.{1,}([0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2})");
	private static Matcher m;
	private ArrayList<String> MACs = new ArrayList<String>();
	private ArrayList<String> IPs = new ArrayList<String>();
	private StringBuilder sbRawARPtabel = new StringBuilder();
	public ARPtabel()
	{
		this.loadArpFile();
	}
	public void update()
	{
		this.loadArpFile();
	}
	public String get()
	{
		return this.sbRawARPtabel.toString();
	}
	public ArrayList<String> getIPs()
	{
		return this.IPs;
	}
	public ArrayList<String> getMACs()
	{
		return this.MACs;
	}
	//update arp-table via an brodcast on lan
	public void sendBrodcast()
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					MulticastSocket ds = new MulticastSocket();
					String packetBody ="FFFFFFFFFFFF";
					InetAddress ia = InetAddress.getByName("192.168.1.255");
					DatagramPacket dp = new DatagramPacket(packetBody.getBytes(), packetBody.length(), ia, 3000);
					ds.setBroadcast(true);
					ds.send(dp);
					ds.close();
				} catch (SocketException e1) {
					e1.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void loadArpFile()
	{
		File f = new File("/proc/net/arp");
		try {
			is = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				m = hasFlag0x2AndMacIsNotNull.matcher(line);
				if(m.find())
				{	
					IPs.add(m.group(1));
					MACs.add(m.group(2));
				}
				sbRawARPtabel.append(line).append("\n");
			}
			reader.close();
			is.close();
		} 
		catch (FileNotFoundException e) {} 
		catch (IOException e) {}
	}
}
