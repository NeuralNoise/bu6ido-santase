/**
 * 
 */
package com.bu6ido.jsantase;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.bu6ido.jsantase.boards.SantasePanel;
import com.bu6ido.jsantase.common.SantaseUtils;
import com.bu6ido.jsantase.notifications.GuiNotification;
import com.bu6ido.jsantase.notifications.INotification;
import com.bu6ido.jsantase.players.AbstractPlayer;
import com.bu6ido.jsantase.players.ComputerPlayer;
import com.bu6ido.jsantase.players.GuiPlayer;

/**
 * @author bu6ido
 *
 */
public class SantaseFrame extends JFrame 
{
	private static final long serialVersionUID = 1L;

	private SantasePanel panel;
	
	private Game game;
	private INotification notification;
	private AbstractPlayer player1, player2;
	
	private Thread gameThread;
	
	public SantaseFrame()
	{
		super(SantaseUtils.SANTASE_VERSION);
		
		setSize(800, 500);
		setLocationRelativeTo(null);
		setIconImage(SantaseUtils.loadImage("jsantase.jpg"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);

		Container cp = getContentPane();
		setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));
		
		game = new Game();
		panel = new SantasePanel(this, game);

		initGame();
		
		GuiNotification guiNotification = (GuiNotification) notification;
		
		JScrollPane spInfo = new JScrollPane(guiNotification.getTextPaneInfo());
		Dimension prefSize = new Dimension(150, spInfo.getPreferredSize().height);
		spInfo.setMinimumSize(prefSize);
		spInfo.setMaximumSize(prefSize);
		
		JSplitPane sppSantase = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, spInfo);
		
		cp.add(sppSantase);
		cp.add(Box.createVerticalStrut(5));
		cp.add(guiNotification.getLblStatus());
		sppSantase.setDividerLocation(600);
		
		addListeners();
	}
	
	protected void initGame()
	{
		notification = new GuiNotification(this);
		player1 = new GuiPlayer(game, "First");
		player2 = new ComputerPlayer(game, "Second");
		game.setNotification(notification);
		game.setBoard(panel);
		game.setPlayer1(player1);
		game.setPlayer2(player2);
	}
	
	protected void addListeners()
	{
		addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				int result = SantaseUtils.dialogYesNo(SantaseFrame.this, "Quit Santase:", "Are you sure you want to quit?", new String[] { "Yes", "No" });
				if (result == 0)
				{
				}
				SantaseFrame.this.setDefaultCloseOperation((result == 0)? EXIT_ON_CLOSE : DO_NOTHING_ON_CLOSE);
			}
		});
	}
	
	protected JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		menuBar.setSize(getSize().width, 30);
		
		JMenu menuGame = new JMenu("JSantase");
		menuGame.setMnemonic('j');
		menuBar.add(menuGame);
		
		JMenuItem miNewGame = new JMenuItem("Start new game");
		miNewGame.setMnemonic('n');
		menuGame.add(miNewGame);
		
		JMenuItem miSettings = new JMenuItem("Settings");
		miSettings.setMnemonic('s');
		menuGame.add(miSettings);
		
		miNewGame.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ev) 
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
						GuiNotification guiNotification = (GuiNotification) notification;
						guiNotification.clearInfo();
						game.startNewGame();
					}
				};
				gameThread.start();
			}
		});
		
		miSettings.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ev) 
			{
				SettingsDialog dlg = new SettingsDialog(SantaseFrame.this);
				dlg.setVisible(true);
			}
		});
		
		return menuBar;
	}
	
	public static void main(String[] args) 
	{
		SantaseFrame frame = new SantaseFrame();
		frame.setVisible(true);
	}
}
