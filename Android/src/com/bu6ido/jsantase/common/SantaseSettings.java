/**
 * 
 */
package com.bu6ido.jsantase.common;

import java.io.Serializable;

/**
 * @author bu6ido
 *
 */
public class SantaseSettings implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private String namePlayer1 = "First";
	private String namePlayer2 = "Second";
	
	private Boolean raising = Boolean.TRUE;
	
	public String getNamePlayer1()
	{
		return namePlayer1;
	}
	
	public void setNamePlayer1(String namePlayer1)
	{
		this.namePlayer1 = namePlayer1;
	}
	
	public String getNamePlayer2()
	{
		return namePlayer2;
	}
	
	public void setNamePlayer2(String namePlayer2)
	{
		this.namePlayer2 = namePlayer2;
	}
	
	public Boolean getRaising()
	{
		return raising;
	}
	
	public void setRaising(Boolean raising)
	{
		this.raising = raising;
	}
}
