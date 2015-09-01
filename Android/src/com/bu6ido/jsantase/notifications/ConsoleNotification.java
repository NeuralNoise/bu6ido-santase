/**
 * 
 */
package com.bu6ido.jsantase.notifications;


/**
 * @author bu6ido
 *
 */
public class ConsoleNotification implements INotification 
{

	@Override
	public void info(String message) 
	{
		System.out.println(message);
	}

	@Override
	public void warning(String message) 
	{
		System.out.println("Warning: " + message);
	}

	@Override
	public void status(String message) 
	{
		System.out.println("Status -> " + message);
	}
}
