/**
 * 
 */
package com.bu6ido.jsantase.common;

import com.bu6ido.jsantase.enums.Cards;
import com.bu6ido.jsantase.enums.Suits;

/**
 * @author bu6ido
 *
 */
public class PlayCard implements Comparable<PlayCard>
{
	private Cards card;
	private Suits suit;
	
	public PlayCard(Cards card, Suits suit)
	{
		this.card = card;
		this.suit = suit;
	}
	
	public Cards getCard()
	{
		return card;
	}
	
	public Suits getSuit()
	{
		return suit;
	}

	@Override
	public int compareTo(PlayCard c) 
	{
		if (this.suit.val() != c.suit.val())
		{
			return this.suit.val() - c.suit.val();
		}
		else
		{
			return this.card.val() - c.card.val();
		}
	}

	@Override
	public String toString() 
	{
		if ((card != null) && (suit != null))
		{
			return card.toString() + suit.toString();
		}
		return "(unknown)";
	}

}
