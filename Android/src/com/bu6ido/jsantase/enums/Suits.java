/**
 * 
 */
package com.bu6ido.jsantase.enums;

/**
 * @author bu6ido
 *
 */
public enum Suits 
{
	CLUB(10), // spatia
	DIAMOND(20), // karo
	HEART(30), // kupa
	SPADE(40); // pika
	
	private Integer val;
	
	Suits(Integer val)
	{
		this.val = val;
	}
	
	public Integer val()
	{
		return this.val;
	}
	
	@Override
	public String toString() 
	{
		if (val == null)
		{
			return "(unknown)";
		}
		if (val.equals(CLUB.val))
		{
			return "\u2663(sp)"; //"\u2667";
		}
		if (val.equals(DIAMOND.val))
		{
			return "\u2666(ka)"; //"\u2662";
		}
		if (val.equals(HEART.val))
		{
			return "\u2665(ku)"; //"\u2661";
		}
		if (val.equals(SPADE.val))
		{
			return "\u2660(pi)"; //"\u2664";
		}
		return "(unknown)";
	}
}
