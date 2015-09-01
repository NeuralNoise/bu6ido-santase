/**
 * 
 */
package com.bu6ido.jsantase.notifications;

import com.bu6ido.jsantase.android_v3.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

/**
 * @author bu6ido
 *
 */
public class SantaseNotification implements INotification 
{
	private boolean ok;
	private Activity activity;
	private StringBuilder infoSB;
	
	public SantaseNotification(Activity activity)
	{
		this.activity = activity;
		infoSB = new StringBuilder();
	}
	
	public void clearLog()
	{
		infoSB.setLength(0);
	}
	
	public String getLog()
	{
		return infoSB.toString();
	}
	
	protected void showWarningDialog(final String message)
	{
		ok = false;
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				AlertDialog dlg = new AlertDialog.Builder(activity).
					setTitle("Warning:").
					setMessage(message).
					setNeutralButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public synchronized void onClick(DialogInterface dialog, int which) 
						{
							ok = true;
						}
					}).create();
				dlg.show();
			}
		};
		
		activity.runOnUiThread(runnable);
			
		while (!ok)
		{
		}
	}
	
	@Override
	public void info(String message) 
	{
		if (infoSB != null)
		{
			infoSB.append(message);
			infoSB.append("\n");
		}
	}

	@Override
	public void warning(String message) 
	{
		showWarningDialog(message);
	}

	@Override
	public void status(final String message) 
	{
		if (activity != null)
		{
			final TextView tvStatus = (TextView) activity.findViewById(R.id.tvStatus);
			if (tvStatus != null)
			{
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tvStatus.setText(message);
					}
				});
			}
		}
	}

}
