/**
 * 
 */
package com.bu6ido.jsantase.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.common.PlayCard;
import com.bu6ido.jsantase.enums.Suits;

/**
 * @author bu6ido
 *
 */
public abstract class AbstractPlayer 
{
	protected Game game;
	protected String name;
	protected List<PlayCard> cards;
	protected List<PlayCard> takenCards;
	protected List<Suits> pairs;
	
	protected boolean isChoosing = false;
	
	public AbstractPlayer(Game game, String name)
	{
		this.game = game;
		this.name = name;
		cards = new ArrayList<PlayCard>();
		takenCards = new ArrayList<PlayCard>();
		pairs = new ArrayList<Suits>();
	}
	
	public void clearData()
	{
		cards.clear();
		takenCards.clear();
		pairs.clear();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void removeCard(PlayCard card)
	{
		cards.remove(card);
	}
	
	public void addCard(PlayCard card)
	{
		cards.add(card);
	}
	
	public List<PlayCard> getCards()
	{
		return cards;
	}
	
	public void setCards(List<PlayCard> cards)
	{
		this.cards = cards;
	}
	
	public void addTakenCard(PlayCard takenCard)
	{
		takenCards.add(takenCard);
	}
	
	public List<PlayCard> getTakenCards()
	{
		return takenCards;
	}
	
	public void setTakenCards(List<PlayCard> takenCards)
	{
		this.takenCards = takenCards;
	}
	
	public void addPair(Suits pairSuit)
	{
		pairs.add(pairSuit);
	}
	
	public int getScore()
	{
		int score = 0;
		if (takenCards != null)
		{
			for (Iterator<PlayCard> iter = takenCards.iterator(); iter.hasNext(); )
			{
				PlayCard card = iter.next();
				if ((card != null) && (card.getCard() != null))
				{
					score += card.getCard().val();
				}
			}
		}
		Suits trump = game.getTrump();
		if (pairs != null)
		{
			for (Iterator<Suits> iter = pairs.iterator(); iter.hasNext(); )
			{
				Suits suit = iter.next();
				if (trump.val() == suit.val())
				{
					score += Game.SCORE_TRUMP_PAIR;
				}
				else
				{
					score += Game.SCORE_PAIR;
				}
			}
		}
		return score;
	}

	public boolean isChoosing()
	{
		return isChoosing;
	}

	// using current cards
	// call game.nextMove(PlayCard)
	public void play()
	{
		Collections.sort(cards);
		game.getNotification().info(this + " is in turn to play.");
		game.getBoard().print();
	}
	
	public String toString() 
	{
		return name;
	}
}
