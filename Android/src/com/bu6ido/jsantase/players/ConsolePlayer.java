/**
 * 
 */
package com.bu6ido.jsantase.players;

import java.util.Scanner;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.common.PlayCard;

/**
 * @author bu6ido
 *
 */
public class ConsolePlayer extends AbstractPlayer 
{
	private Scanner scanner;
	public ConsolePlayer(Game game, String name) 
	{
		super(game, name);
		scanner = new Scanner(System.in);
	}

	@Override
	public void play() 
	{
		super.play();
		
		PlayCard card = null;
		int choice;
		String str;
		isChoosing = true;
		do
		{
			System.out.print("Please choose from (1.." + cards.size() + ", (C)lose, Change (T)rump): ");
			str = scanner.next();
			if (str.equalsIgnoreCase("c"))
			{
				game.closeGame(this);
				continue;
			}
			if (str.equalsIgnoreCase("t"))
			{
				game.changeTrump(this);
				game.getNotification().info(this + " changed the trump card.");
				continue;
			}
			try
			{
				choice = Integer.parseInt(str);
			}
			catch (NumberFormatException nfe)
			{
				choice = 0;
			}
			if ((choice >= 1) && (choice <= cards.size()))
			{
				card = cards.get(choice - 1);
			}
			else
			{
				card = null;
			}
		} 
		while (!game.validateCard(this, card, true));
		isChoosing = false;
		
		game.getNotification().info(this + " chose card " + card);
		
		game.nextMove(card);
	}

}
