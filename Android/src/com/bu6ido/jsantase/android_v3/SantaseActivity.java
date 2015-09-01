/**
 * 
 */
package com.bu6ido.jsantase.android_v3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.common.SantaseSettings;
import com.bu6ido.jsantase.common.SantaseUtils;
import com.bu6ido.jsantase.notifications.INotification;
import com.bu6ido.jsantase.notifications.SantaseNotification;
import com.bu6ido.jsantase.players.AbstractPlayer;
import com.bu6ido.jsantase.players.ComputerPlayer;
import com.bu6ido.jsantase.players.GuiPlayer;

/**
 * @author bu6ido
 *
 */
public class SantaseActivity extends Activity 
{
	private SantaseView view;
	
	private Game game;
	private INotification notification;
	private AbstractPlayer player1, player2;
	
	private Thread gameThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.santase_activity);
		
		view = (SantaseView) findViewById(R.id.viewSantase);
		
		initGame();
	}
	
	protected void initGame()
	{
		game = new Game();
		view.setGame(game);
		notification = new SantaseNotification(this);
		player1 = new GuiPlayer(game, "First");
		player2 = new ComputerPlayer(game, "Second");
		game.setNotification(notification);
		game.setBoard(view);
		game.setPlayer1(player1);
		game.setPlayer2(player2);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_santase, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
			case R.id.menuNewGame:
				startGame();
				break;
			case R.id.menuViewLog:
				viewGameLog();
				break;
			case R.id.menuSettings:
				changeSettings();
				break;
			case R.id.menuAbout:
				about();
				break;
			default:
				break;
		}
		return true;
	}
	
	protected void startGame()
	{
		if (gameThread != null)
		{
			game.setIsInterrupted(true);
			SantaseUtils.sleep(250l);
			gameThread = null;
		}

		gameThread = new Thread()
		{
			@Override
			public void run() 
			{
				SantaseNotification sanNotif = (SantaseNotification) notification;
				if (sanNotif != null)
				{
					sanNotif.clearLog();
				}
				SantaseSettings settings = SantaseUtils.loadSettings(SantaseActivity.this);
				game.setSettings(settings);
				game.startNewGame();
			}
		};
		gameThread.start();
	}
	
	protected void viewGameLog()
	{
		if (notification == null)
		{
			return;
		}
		if (!(notification instanceof SantaseNotification))
		{
			return;
		}
		SantaseNotification sanNotif = (SantaseNotification) notification;
		
		AlertDialog dlgLog = new AlertDialog.Builder(this).
			setTitle("Game log:").
			setMessage(sanNotif.getLog()).
			setNeutralButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dlg, int which) 
				{
					dlg.dismiss();
				}
			}).create();
		dlgLog.show();
	}
	
	protected void changeSettings()
	{
		final SantaseSettings settings = SantaseUtils.loadSettings(this);
		
		final Dialog dlgSettings = new Dialog(this);
		dlgSettings.setTitle("Game settings:");
		dlgSettings.setContentView(R.layout.santase_settings);
		final EditText etNamePlayer1 = (EditText) dlgSettings.findViewById(R.id.etNamePlayer1);
		final EditText etNamePlayer2 = (EditText) dlgSettings.findViewById(R.id.etNamePlayer2);
		final CheckBox chkRaising = (CheckBox) dlgSettings.findViewById(R.id.chkRaising);
		final Button btnSave = (Button) dlgSettings.findViewById(R.id.btnSave);
		final Button btnCancel = (Button) dlgSettings.findViewById(R.id.btnCancel);

		etNamePlayer1.setText(settings.getNamePlayer1());
		etNamePlayer2.setText(settings.getNamePlayer2());
		chkRaising.setChecked(settings.getRaising());

		btnSave.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) 
				{
					settings.setNamePlayer1(etNamePlayer1.getText().toString());
					settings.setNamePlayer2(etNamePlayer2.getText().toString());
					settings.setRaising(chkRaising.isChecked());
					SantaseUtils.saveSettings(SantaseActivity.this, settings);
					dlgSettings.dismiss();
					Toast.makeText(SantaseActivity.this,
						"Settings have been saved !!! Please restart the game for changes to take effect !!!", 
						Toast.LENGTH_LONG).show();
				}
			});
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dlgSettings.cancel();
			}
		});
		
		dlgSettings.show();
	}
	
	protected void about()
	{
		Dialog dlgAbout = new Dialog(this)
		{
			protected void onCreate(Bundle savedInstanceState) 
			{
				super.onCreate(savedInstanceState);
				setTitle("About:");
				setContentView(R.layout.about_jsantase);
				
				Button btnOK = (Button) findViewById(R.id.btnOK);
				btnOK.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						dismiss();
					}
				});
			}
		};
		dlgAbout.show();
	}
}
