/**
 * 
 */
package com.bu6ido.jsantase.notifications;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

import com.bu6ido.jsantase.common.SantaseUtils;

/**
 * @author bu6ido
 *
 */
public class GuiNotification implements INotification 
{
	private JFrame frame;
	private JLabel lblStatus;
	private JTextPane tpInfo;
	
	public GuiNotification(JFrame frame)
	{
		this.frame = frame;
		lblStatus = new JLabel();
		tpInfo = new JTextPane();
		Dimension prefSize = new Dimension(150, tpInfo.getPreferredSize().height);
		tpInfo.setMinimumSize(prefSize);
		tpInfo.setMaximumSize(prefSize);
		tpInfo.setEditable(false);
	}
	
	@Override
	public void info(final String message) 
	{
		SantaseUtils.waitUntilFinish(new Runnable() {
			
			@Override
			public void run() {
				appendText(message + "\n", null);
			}
		});
	}

	@Override
	public void warning(final String message) 
	{
		SantaseUtils.waitUntilFinish(new Runnable() {
			
			@Override
			public void run() {
				SantaseUtils.warningDialog(frame, "Warning:", message);
			}
		});
	}

	@Override
	public void status(final String message) 
	{
		SantaseUtils.waitUntilFinish(new Runnable() {
			
			@Override
			public void run() {
				lblStatus.setText(message);
			}
		});
	}
	
	public synchronized JLabel getLblStatus()
	{
		return lblStatus;
	}
	
	public synchronized JTextPane getTextPaneInfo()
	{
		return tpInfo;
	}
	
	protected synchronized void appendText(String text, AttributeSet attr)
	{
		try
		{
			tpInfo.setEditable(true);
			StyledDocument doc = tpInfo.getStyledDocument();
			//tpChat.setCaretPosition(doc.getLength());
			//tpChat.replaceSelection(text);
			
			doc.insertString(doc.getLength(), text, attr);
			if (doc != null) 
			{
				Rectangle rect = tpInfo.modelToView(doc.getLength());
				if (rect != null)
					tpInfo.scrollRectToVisible(rect);
			}
			tpInfo.setEditable(false);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void clearInfo()
	{
		tpInfo.setText("");
	}
}
