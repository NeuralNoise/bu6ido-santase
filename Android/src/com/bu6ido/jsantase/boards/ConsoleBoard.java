/**
 * 
 */
package com.bu6ido.jsantase.boards;

import java.util.List;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.common.PlayCard;

/**
 * @author bu6ido
 *
 */
public class ConsoleBoard implements IBoard {

	private Game game;
	
	public ConsoleBoard(Game game)
	{
		this.game = game;
	}
	
	@Override
	public void print() 
	{
		List<PlayCard> cards = game.isOne()? 
			game.getPlayer1().getCards() : game.getPlayer2().getCards();
		System.out.println("Your cards are: ");
		for (int i=0; i<cards.size(); i++)
		{
			PlayCard card = cards.get(i);
			System.out.print(card + " ");
		}
		System.out.println();
	}

	@Override
	public void fireCardMove(PlayCard card) 
	{
		
	}
	
	@Override
	public void fireTakeCards(boolean isOne) 
	{
		
	}
}
