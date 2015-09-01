/**
 * 
 */
package com.bu6ido.jsantase.enums;

/**
 * @author bu6ido
 *
 */
public enum Cards 
{
	NINE(0), // devet
	JACK(2), // vale
	QUEEN(3), // dama
	KING(4), // pop
	TEN(10), // deset
	ACE(11); // asak
	
	private Integer val;
	
	Cards(Integer val)
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
		if (val.equals(NINE.val))
		{
			return "9";
		}
		if (val.equals(JACK.val))
		{
			return "J";
		}
		if (val.equals(QUEEN.val))
		{
			return "Q";
		}
		if (val.equals(KING.val))
		{
			return "K";
		}
		if (val.equals(TEN.val))
		{
			return "10";
		}
		if (val.equals(ACE.val))
		{
			return "A";
		}
		return "(unknown)";
	}
}
