package com.voidcode.nshare;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.voidcode.nshare.R;

public class CopyActivity extends Activity{
	private TextView tvCopyText;
	private ClipboardManager clipboard;
	private String ip;
	private String text;
	private ActionBar actionbar;
	private Typeface uFont;
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedIntentceState)
	{
		super.onCreate(savedIntentceState);
		setContentView(R.layout.activity_copy);

		setTitle("Nshare-copy");
		//setup actionbar
		actionbar = (ActionBar) getActionBar();
		actionbar.setHomeButtonEnabled(true);
				
		//get ubuntu-font
		uFont = Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf");
		TextView action_bar_title = (TextView) findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
		action_bar_title.setTypeface(uFont);
		
		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		tvCopyText = (TextView)findViewById(R.id.tvCopyText);
		tvCopyText.setTypeface(uFont);
		tvCopyText.setText("Nothing to copy!");
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			ip = extras.getString("ip");
			text = extras.getString("text");
			if(ip != null && text != null)
			{
				setTitle("From "+ip);
				tvCopyText.setText(text);
				if(text.startsWith("http"))
				{
					tvCopyText.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(text));
							startActivity(i);
							finish();
						}
					});
				}
				else
				{
					tvCopyText.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							saveInClipBoard(text);
							Toast.makeText(v.getContext(), "Copy to clipholder", Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				}
			}
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void saveInClipBoard(String text)
	{
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
		    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		    clipboard.setText(text);
		} else {
		    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
			clipboard.setPrimaryClip(clip);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		 	case android.R.id.home:
		 		new AlertDialog.Builder(this)
	            .setTitle("Close "+getString(R.string.app_name)+"?")
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
