/**
 * 
 */
package com.bu6ido.jsantase.players;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.common.PlayCard;
import com.bu6ido.jsantase.common.SantaseUtils;


/**
 * @author bu6ido
 *
 */
public class GuiPlayer extends AbstractPlayer 
{
	private PlayCard card;
	
	public GuiPlayer(Game game, String name) 
	{
		super(game, name);
	}

	public synchronized void setCard(PlayCard card)
	{
		this.card = card;
	}
	
	@Override
	public void play() 
	{
		super.play();
		
		isChoosing = true;
		
		card = null;
		
		while ((card == null) || !game.validateCard(this, card, true))
		{
			if (game.getIsInterrupted())
			{
				return;
			}
			if (card != null)
			{
				card = null;
			}
			SantaseUtils.sleep(10);
		}
		
		game.getBoard().fireCardMove(card);
		
		isChoosing = false;
		
		game.getNotification().info(GuiPlayer.this + " chose card " + card);
		
		game.nextMove(card);
	}
}
