package com.voidcode.nshare;

public class Device {
	private String name;
	private String ip;
	public Device(){}
	public Device(String Name, String Ip)
	{
		this.name = Name;
		this.ip = Ip;
	}
	//NAME
	public String getName(){
		return name;
	}
	public void setName(String Name)
	{
		this.name = Name;
	}
	//IP
	public String getIp(){
		return ip;
	}
	public void setIp(String Ip){
		this.ip = Ip;
	}
}