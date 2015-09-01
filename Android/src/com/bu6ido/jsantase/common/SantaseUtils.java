/**
 * 
 */
package com.bu6ido.jsantase.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.graphics.Point;

/**
 * @author bu6ido
 *
 */
public class SantaseUtils 
{
	public static final String SANTASE_VERSION = "JSantase(ver 1.0)";
	
	// cards images from: 
	// http://www.jfitz.com/cards/
	
	private SantaseUtils()
	{
	}

	/*public static Image loadImage(String path)
	{
		Image result = null;
		try
		{
			result = ImageIO.read(SantaseUtils.class.getResource(path));
			//result = ImageIO.read(new File(path));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
	
	public static Image scaleImage(Image original, int width, int height)
	{
		Image scaledImage = original.getScaledInstance(width, height, Image.SCALE_FAST);
        BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = imageBuff.createGraphics();
        g.drawImage(scaledImage, 0, 0, new Color(0,0,0), null);
        g.dispose();
        return imageBuff;
	}*/

	public static Point[] linePath(int x1, int y1, int x2, int y2, int steps)
	{
		if (steps == 0)
		{
			return null;
		}
		Point[] result = new Point[steps];
		result[0] = new Point(x1, y1);
		steps--;
		for (int i=1; i<=steps; i++)
		{
			Point p = new Point((int) (x1 + i * (x2 - x1) / (double)steps), (int)(y1 + i * (y2 - y1) / (double)steps));
			result[i] = p;
		}
		return result;
	}
	
	/*public static int dialogYesNo(JFrame frame, String title, String message, Object[] options)
	{
		JOptionPane pane = new JOptionPane(message);
		pane.setOptions(options);
		JDialog dialog = pane.createDialog(frame, title);
		dialog.setVisible(true);
		Object obj = pane.getValue();
		for (int i=0; i<options.length; i++)
		{
			if (options[i].equals(obj))
			{
				return i;
			}
		}
		return -1;
	}
	
	public static String dialogInputText(JFrame frame, String title, String message)
	{
		String res = JOptionPane.showInputDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
		return res;
	}
	
	public static void warningDialog(JFrame frame, String title, String message)
	{
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
	}*/
	
	// sleep current thread for some milliseconds
	public static final void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (Exception ex)
		{
		}
	}
	
	// wait until code in Runnable finished
	/*public static void waitUntilFinish(Runnable runnable)
	{
		if (runnable == null)
			return;
		
		if (EventQueue.isDispatchThread())
		{
			runnable.run();
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(runnable);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}*/

	public static Serializable loadObject(Context ctx, String fileName)
	{
		try
		{
			FileInputStream fis = ctx.openFileInput(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Serializable result = (Serializable) ois.readObject();
			ois.close();
			return result;
		}
		catch (Exception ex)
		{
			//ex.printStackTrace();
		}
		return null;
	}
	
	public static void saveObject(Context ctx, String fileName, Serializable obj)
	{
		try
		{
			FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static String getSettingsPath()
	{
		String result = "jsantase.dat";
		return result;
	}
	
	public static SantaseSettings loadSettings(Context ctx)
	{
		Serializable res = loadObject(ctx, getSettingsPath());
		if ((res != null) && (res instanceof SantaseSettings))
		{
			return (SantaseSettings) res;
		}
		return new SantaseSettings();
	}
	
	public static void saveSettings(Context ctx, SantaseSettings settings)
	{
		saveObject(ctx, getSettingsPath(), settings);
	}

}
