package com.voidcode.nshare;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.voidcode.nshare.R;

public class DeviceAdapter extends ArrayAdapter<Device> {
	private ArrayList<Device> devices;
	private Typeface typeface;
	public DeviceAdapter(Context context, int resource, ArrayList<Device> devices, Typeface typeface) {
		super(context, resource, devices);
		this.devices = devices;
		this.typeface = typeface;
	}
	public View getView(int pos, View convertView, ViewGroup parent )
	{
		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.deviceadapter_item, null);
		}	

				/*
				 * Recall that the variable position is sent in as an argument to this method.
				 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
				 * iterates through the list we sent it)
				 * 
				 * Therefore, i refers to the current Item object.
				 */
		Device d = devices.get(pos);
		if(d != null)
		{
			TextView tvDeviceadaptorName = (TextView) v.findViewById(R.id.tvDeviceadaptorName);
			if(tvDeviceadaptorName != null)
			{
				tvDeviceadaptorName.setTypeface(typeface);
				if(d.getIp() != d.getName())//it look better, no same/dobble info
					tvDeviceadaptorName.setText(d.getName());
				else
					tvDeviceadaptorName.setText("Unknown");
			}
			TextView tvDeviceadaptorIp = (TextView) v.findViewById(R.id.tvDeviceadaptorIp);
			if(tvDeviceadaptorName != null)
			{
				tvDeviceadaptorName.setTypeface(typeface);
				tvDeviceadaptorIp.setText(d.getIp());
			}
		}
		return v;
	}
}

