package com.voidcode.nshare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;


public class SendPacketStackTask extends AsyncTask<Void, Integer, Boolean> {
	private String TAG = "sendPacketTask";
	private ArrayList<String> stackOfToIPs;
	private String data;
	private DatagramSocket ds;
	private DatagramPacket dp;
	private Boolean isDone = false;
	public SendPacketStackTask(String Data, ArrayList<String> StackOfToIPs) throws IOException
	{
		this.data = Data;
		this.stackOfToIPs = StackOfToIPs;
		this.ds = new DatagramSocket();
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		try 
		{
			for(String ip :stackOfToIPs)
			{
				dp = new DatagramPacket(data.getBytes(), data.length(), InetAddress.getByName(ip), 12345);
				ds.setBroadcast(true);
				ds.send(dp);
				Log.d(TAG, "send packet to-->ip: "+ip);
				Thread.sleep(1);
			}
			isDone = true;
		} catch (SocketException e) {
			isDone = false;
			e.printStackTrace();
		} catch (UnknownHostException e) {
			isDone = false;
			e.printStackTrace();
		} catch (IOException e) {
			isDone = false;
			e.printStackTrace();
		} catch (InterruptedException e) {
			isDone = false;
			e.printStackTrace();
		}
		finally
		{
			ds.close();
		}
		return isDone;
	}
}
