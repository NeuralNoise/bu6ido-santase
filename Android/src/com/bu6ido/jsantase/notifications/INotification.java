/**
 * 
 */
package com.bu6ido.jsantase.notifications;

/**
 * @author bu6ido
 *
 */
public interface INotification 
{
	public void info(String message);
	public void warning(String message);
	public void status(String message);
}
