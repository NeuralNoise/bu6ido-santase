/**
 * 
 */
package com.bu6ido.jsantase.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.common.PlayCard;
import com.bu6ido.jsantase.enums.Cards;
import com.bu6ido.jsantase.enums.Suits;

/**
 * @author bu6ido
 *
 */
public class ComputerPlayer extends AbstractPlayer 
{
	private PlayCard card;
	private Comparator<PlayCard> cmpNoSuit;
	
	public ComputerPlayer(Game game, String name) 
	{
		super(game, name);
		
		cmpNoSuit = new Comparator<PlayCard>() 
		{
			@Override
			public int compare(PlayCard c1, PlayCard c2) 
			{
				return c1.getCard().val() - c2.getCard().val();
			}
		}; 
	}

	protected void checkForTrumpCardChange()
	{
		if (game.getIsClosed())
		{
			return;
		}
		if (!game.firstCardToDrop())
		{
			return;
		}
		if ((game.getTrumpCard() != null) && 
			(game.getTrumpCard().getCard().val() == Cards.NINE.val()) )
		{
			return;
		}
		if (game.deckNotEnough())
		{
			return;
		}
		// check if there is NINE from the trump in the player cards
		PlayCard nineTrump = null;
		for (Iterator<PlayCard> iter = cards.iterator(); iter.hasNext(); )
		{
			PlayCard card = iter.next();
			if ((card.getCard().val() == Cards.NINE.val()) &&
				(card.getSuit().val() == game.getTrump().val()) )
			{
				nineTrump = card;
				break;
			}
		}
		if (nineTrump == null)
		{
			return;
		}
		game.changeTrump(this);
		game.getNotification().info(this + " changed the trump card.");
		game.getNotification().warning(this + " changed the trump card.");
	}
	
	protected void checkForGameClose()
	{
		if (game.getIsClosed())
		{
			return;
		}
		if (game.deckNotEnough())
		{
			return;
		}
		if (!game.firstCardToDrop())
		{
			return;
		}
		int currentScore = getScore();

		AbstractPlayer opponent = this.equals(game.getPlayer1())? game.getPlayer2() : game.getPlayer1();
		
		List<PlayCard> trumpCards = new ArrayList<PlayCard>();
		int myTrumpScore = 0;
		for (Iterator<PlayCard> iter = cards.iterator(); iter.hasNext(); )
		{
			PlayCard workCard = iter.next();
			if (workCard.getSuit().val() == game.getTrump().val())
			{
				trumpCards.add(workCard);
				myTrumpScore += workCard.getCard().val();
			}
		}
		
		List<PlayCard> oppTrumpCards = new ArrayList<PlayCard>();
		int oppTrumpScore = 0;
		for (Iterator<PlayCard> iter = opponent.getCards().iterator(); iter.hasNext(); )
		{
			PlayCard workCard = iter.next();
			if (workCard.getSuit().val() == game.getTrump().val())
			{
				oppTrumpCards.add(workCard);
				oppTrumpScore += workCard.getCard().val();
			}
		}
		
		if (myTrumpScore <= oppTrumpScore)
		{
			return;
		}
		
		int myStrongScore = 0;
		for (Iterator<PlayCard> iter = cards.iterator(); iter.hasNext(); )
		{
			PlayCard workCard = iter.next();
			if ((workCard.getSuit().val() != game.getTrump().val()) &&
				((workCard.getCard().val() == Cards.TEN.val()) ||
				 (workCard.getCard().val() == Cards.ACE.val())) )
			{
				myStrongScore += workCard.getCard().val();
			}
		}
		
		int oppStrongScore = 0;
		for (Iterator<PlayCard> iter = opponent.getCards().iterator(); iter.hasNext(); )
		{
			PlayCard workCard = iter.next();
			if ((workCard.getSuit().val() != game.getTrump().val()) &&
				((workCard.getCard().val() == Cards.TEN.val()) ||
				 (workCard.getCard().val() == Cards.ACE.val())) )
			{
				oppStrongScore += workCard.getCard().val();
			}
		}
		
		if (myStrongScore < oppStrongScore)
		{
			return;
		}
		
		//System.out.println("Score = " + currentScore + ", trumpScore = " + myTrumpScore + ", strongScore = " + myStrongScore);
		if ((currentScore + myTrumpScore + myStrongScore + Cards.TEN.val()) < Game.FINAL_SCORE)
		{
			return;
		}
		
		game.closeGame(this);
	}
	
	@Override
	public void play() 
	{
		super.play();
		
		isChoosing = true;
		
		game.getNotification().info(this + " is thinking...");
		
		checkForTrumpCardChange();
		checkForGameClose();
		
		card = null;
		List<PlayCard> availCards = new ArrayList<PlayCard>();
		for (Iterator<PlayCard> iter = cards.iterator(); iter.hasNext(); )
		{
			PlayCard workCard = iter.next();
			if (game.validateCard(this, workCard, false))
			{
				availCards.add(workCard);
			}
		}
		if (availCards.size() == 1)
		{
			card = availCards.get(0);
		}
		else
		{
			// 4 different strategies depending on if this player is first or second
			// and if the game is closed or not
			AbstractPlayer opponent = this.equals(game.getPlayer1())? game.getPlayer2() : game.getPlayer1();
			if (game.firstCardToDrop())
			{
				if (game.getIsClosed())
				{
					// first to play and game closed
					// Strategy 1:
					// 1. If you have card that is stronger than the strongest opponent's card
					// from this suit(and of course opponent has cards from this suit) - the hand 
					// is yours, no matter the suit(first the trump suit, then for other suits)
					// 2. If you have 20 or 40 - announce it now(for 1. if you have King and Queen as
					// strongest cards - play the queen first)
					// 3. Play the weakest card of a suit the opponent doesn't have(he will trump if
					// he has trump cards)
					// 4. Play the weakest trump that is weaker than the opponent strongest trump but
					// stronger than his others trump cards
					
					for (int i=0; i<Suits.values().length; i++)
					{
						Suits workSuit = Suits.values()[i];
						
						int max = 0;
						PlayCard maxCard = null;
						for (int j=0; j<availCards.size(); j++)
						{
							PlayCard workCard = availCards.get(j);
							if (workCard.getSuit().val() == workSuit.val())
							{
								if (workCard.getCard().val() > max)
								{
									max = workCard.getCard().val();
									maxCard = workCard;
								}
							}
						}
						
						if (maxCard != null)
						{
							boolean hasStronger = false;
							int countSuit = 0;
							for (int j=0; j<opponent.getCards().size(); j++)
							{
								PlayCard workCard = opponent.getCards().get(j);
								if (workCard.getSuit().val() == workSuit.val())
								{
									countSuit++;
									if (workCard.getCard().val() > maxCard.getCard().val())
									{
										hasStronger = true;
										break;
									}
								}
							}
							
							if (!hasStronger && (countSuit > 0))
							{
								card = maxCard;
								break;
							}
						}
					}
					
					if (card == null)
					{
						// checks for 20 or 40 and play the queen
						List<PlayCard> queens = new ArrayList<PlayCard>();
						List<Integer> scores = new ArrayList<Integer>();
						for (int i=0; i<availCards.size(); i++)
						{
							PlayCard workCard = availCards.get(i);
							if (workCard.getCard().val() == Cards.QUEEN.val())
							{
								for (int j=0; j<availCards.size(); j++)
								{
									PlayCard workCard2 = availCards.get(j);
									if ((workCard.getSuit().val() == workCard2.getSuit().val()) &&
										(workCard2.getCard().val() == Cards.KING.val()) )
									{
										queens.add(workCard);
										if (workCard2.getSuit().val() == game.getTrump().val())
										{
											scores.add(Game.SCORE_TRUMP_PAIR);
										}
										else
										{
											scores.add(Game.SCORE_PAIR);
										}
									}
								}
							}
						}
						int max = 0;
						for (int i=0; i<Math.min(queens.size(), scores.size()); i++)
						{
							if (max < scores.get(i))
							{
								max = scores.get(i);
								card = queens.get(i);
							}
						}
					}
					
					if (card == null)
					{
						// suits of cards that the opponent has
						List<Suits> hasSuits = new ArrayList<Suits>();
						for (int i=0; i<opponent.getCards().size(); i++)
						{
							PlayCard workCard = opponent.getCards().get(i);
							if (!hasSuits.contains(workCard.getSuit()))
							{
								hasSuits.add(workCard.getSuit());
							}
						}
						
						PlayCard weakest = null;
						for (int i=0; i<availCards.size(); i++)
						{
							PlayCard workCard = availCards.get(i);
							if (!hasSuits.contains(workCard.getSuit()))
							{
								if (weakest == null)
								{
									weakest = workCard;
								}
								
								if (workCard.getCard().val() < weakest.getCard().val())
								{
									weakest = workCard;
								}
							}
						}
						
						card = weakest;
					}
					
					if (card == null)
					{
						List<PlayCard> trumpCards = new ArrayList<PlayCard>();
						for (int i=0; i<availCards.size(); i++)
						{
							PlayCard workCard = availCards.get(i);
							if (workCard.getSuit().val() == game.getTrump().val())
							{
								trumpCards.add(workCard);
							}
						}
						Collections.sort(trumpCards);
						
						List<PlayCard> oppTrumpCards = new ArrayList<PlayCard>();
						for (int i=0; i<opponent.getCards().size(); i++)
						{
							PlayCard workCard = opponent.getCards().get(i);
							if (workCard.getSuit().val() == game.getTrump().val())
							{
								oppTrumpCards.add(workCard);
							}
						}
						Collections.sort(oppTrumpCards);
						
						//TODO sometimes exceptions
						if ((oppTrumpCards.size() < 2) && (trumpCards.size() > 0))
						{
							card = trumpCards.get(0);
						}
						else
						if (oppTrumpCards.size() >= 2)
						{
							PlayCard firstTrump = oppTrumpCards.get(oppTrumpCards.size() - 1);
							PlayCard secondTrump = oppTrumpCards.get(oppTrumpCards.size() - 2);
							List<PlayCard> weakerCards = new ArrayList<PlayCard>();
							for (int i=0; i<trumpCards.size(); i++)
							{
								PlayCard workCard = trumpCards.get(i);
								if ((workCard.getCard().val() > secondTrump.getCard().val()) &&
									(workCard.getCard().val() < firstTrump.getCard().val()) )
								{
									weakerCards.add(workCard);
								}
							}
							Collections.sort(weakerCards);
							if ((weakerCards != null) && (weakerCards.size() > 0))
							{
								card = weakerCards.get(0);
							}
						}
					}
				}
				else
				{
					// first to play and not closed
					// Strategy 2:
					// 1. Play 20 or 40 if you have that
					// 2. Play the weakest non-trump card or the weakest card
					// from the suit the opponent doesn't have
					
					// checks for 20 or 40 and play the queen
					List<PlayCard> queens = new ArrayList<PlayCard>();
					List<Integer> scores = new ArrayList<Integer>();
					for (int i=0; i<availCards.size(); i++)
					{
						PlayCard workCard = availCards.get(i);
						if (workCard.getCard().val() == Cards.QUEEN.val())
						{
							for (int j=0; j<availCards.size(); j++)
							{
								PlayCard workCard2 = availCards.get(j);
								if ((workCard.getSuit().val() == workCard2.getSuit().val()) &&
									(workCard2.getCard().val() == Cards.KING.val()) )
								{
									queens.add(workCard);
									if (workCard2.getSuit().val() == game.getTrump().val())
									{
										scores.add(Game.SCORE_TRUMP_PAIR);
									}
									else
									{
										scores.add(Game.SCORE_PAIR);
									}
								}
							}
						}
					}
					int max = 0;
					for (int i=0; i<Math.min(queens.size(), scores.size()); i++)
					{
						if (max < scores.get(i))
						{
							max = scores.get(i);
							card = queens.get(i);
						}
					}
					
					// no 20 or 40 so it plays some weak card
					if (card == null)
					{
						// suits of cards that the opponent has
						List<Suits> hasSuits = new ArrayList<Suits>();
						for (int i=0; i<opponent.getCards().size(); i++)
						{
							PlayCard workCard = opponent.getCards().get(i);
							if (!hasSuits.contains(workCard.getSuit()))
							{
								hasSuits.add(workCard.getSuit());
							}
						}
	
						// chooses the weakest card that is not trump
						// or the card of suit that the opponent doesn't have
						PlayCard weakest = null;
						for (int i=0; i<availCards.size(); i++)
						{
							PlayCard workCard = availCards.get(i);
							if (workCard.getSuit().val() == game.getTrump().val())
							{
								continue;
							}
							if (weakest == null)
							{
								weakest = workCard;
							}
							if (workCard.getCard().val() < weakest.getCard().val())
							{
								weakest = workCard;
							}
							if ((workCard.getCard().val() == weakest.getCard().val()) &&
								!hasSuits.contains(workCard.getSuit()))
							{
								weakest = workCard;
							}
						}
						
						card = weakest;
					}
				}
			}
			else
			{
				PlayCard oppCard = (game.getOneCard() != null)? game.getOneCard() : game.getTwoCard();
				
				if (game.getIsClosed())
				{
			        // second to play and game closed
			        // Strategy 3:
			        // 1. Determine our cards from the same suit as the opponent card
			        // 2. If there are cards from 1:
			        //   a. We have a stronger card than the opponent - put the strongest card
			        //   b. We do not have a stronger card than the opponent card - put the weakest
			        // 3. If we don't have cards from 1:
			        //   a. Opponent card is trump - put the weakest card we have
			        //   b. Opponent card is not trump - put the strongest trump? we have or the
			        //      weakest card if we don't have trumps
					
					// finds all cards with the same suit as the opponent
					List<PlayCard> sameSuitCards = new ArrayList<PlayCard>();
					for (int i=0; i<availCards.size(); i++)
					{
						PlayCard workCard = availCards.get(i);
						if (workCard.getSuit().val() == oppCard.getSuit().val())
						{
							sameSuitCards.add(workCard);
						}
					}
					Collections.sort(sameSuitCards);
					
					// finds the trump cards we have
					List<PlayCard> trumpCards = new ArrayList<PlayCard>();
					for (int i=0; i<availCards.size(); i++)
					{
						PlayCard workCard = availCards.get(i);
						if (workCard.getSuit().val() == game.getTrump().val())
						{
							trumpCards.add(workCard);
						}
					}
					Collections.sort(trumpCards);
					
					// 2.
					if (sameSuitCards.size() > 0)
					{
						PlayCard strongest = sameSuitCards.get(sameSuitCards.size() - 1);
						PlayCard weakest = sameSuitCards.get(0);
						if (strongest.getCard().val() > oppCard.getCard().val())
						{
							// 2.a
							card = strongest;
						}
						else
						{
							// 2.b
							card = weakest;
						}
					}
					else
					{
						// 3.
						if (oppCard.getSuit().val() == game.getTrump().val())
						{
							// 3.a
							Collections.sort(availCards, cmpNoSuit);
							card = availCards.get(0);
						}
						else
						{
							// 3.b
							if (trumpCards.size() > 0)
							{
								// the strongest trump? we have
								card = trumpCards.get(trumpCards.size() - 1);
							}
							else
							{
								// or the weakest card we have
								Collections.sort(availCards, cmpNoSuit);
								card = availCards.get(0);
							}
						}
					}
				}
				else
				{
					// second to play and game not closed
					// Strategy 4:
					// 1. Removes potential 20 or 40 pairs from the list of available cards
					// 2. Checks if the strongest card of the same suit as oppoenent's card
					// is stronger than opponent's card. If so - takes it
					// 3. If opponent's card is ACE or TEN - takes it with trump(the strongest trump?)
					// 4. If opponent's card is not strong or we don't have trumps to play
					// give him the weakest not-trump card we have
					
					// remove potential 20 or 40 from the list of available cards
					// they shouldn't be played
					List<PlayCard> pairCards = new ArrayList<PlayCard>();
					for (int i=0; i<availCards.size(); i++)
					{
						PlayCard workCard = availCards.get(i);
						if (workCard.getCard().val() == Cards.QUEEN.val())
						{
							for (int j=0; j<availCards.size(); j++)
							{
								PlayCard workCard2 = availCards.get(j);
								if ((workCard2.getCard().val() == Cards.KING.val()) &&
									(workCard.getSuit().val() == workCard2.getSuit().val()) )
								{
									pairCards.add(workCard);
									pairCards.add(workCard2);
									break;
								}
							}
						}
					}
					availCards.removeAll(pairCards);
					
					// finds all cards with the same suit as the opponent
					List<PlayCard> sameSuitCards = new ArrayList<PlayCard>();
					for (int i=0; i<availCards.size(); i++)
					{
						PlayCard workCard = availCards.get(i);
						if (workCard.getSuit().val() == oppCard.getSuit().val())
						{
							sameSuitCards.add(workCard);
						}
					}
					Collections.sort(sameSuitCards);
					
					// check if strongest card from the same suit is stronger than the opponent card
					if ((sameSuitCards != null) && (sameSuitCards.size() > 0))
					{
						PlayCard strongest = sameSuitCards.get(sameSuitCards.size() - 1);
						
						if (strongest.getCard().val() > oppCard.getCard().val())
						{
							card = strongest;
						}
					}
					
					List<PlayCard> trumpCards = new ArrayList<PlayCard>();
					for (int i=0; i<availCards.size(); i++)
					{
						PlayCard workCard = availCards.get(i);
						if (workCard.getSuit().val() == game.getTrump().val())
						{
							trumpCards.add(workCard);
						}
					}

					if (card == null)
					{
						if (oppCard.getSuit().val() != game.getTrump().val())
						{
							if ((oppCard.getCard().val() == Cards.ACE.val()) ||
								(oppCard.getCard().val() == Cards.TEN.val()) )
							{
								// if opponent card is not trump and is strong, try to take it with trump
								// take with the strongest trump card
								if ((trumpCards != null) && (trumpCards.size() > 0))
								{
									Collections.sort(trumpCards);
									card = trumpCards.get(trumpCards.size() - 1);
								}
							}
						}
					}
					
					if (card == null)
					{
						// opponent card is not strong or we don't have trumps to play
						// return the weakest card we have(without trumps), no matter the suit
						availCards.removeAll(trumpCards);
						Collections.sort(availCards, cmpNoSuit);
						card = availCards.get(0);
					}
				}
			}
		}
		
		// if not choosed - random card of availables
		if (card == null)
		{
			Random rand = new Random(System.currentTimeMillis());
			card = availCards.get(rand.nextInt(availCards.size()));
		}
		
		if (!game.validateCard(this, card, true))
		{
			return;
		}
		
		game.getBoard().fireCardMove(card);
		
		isChoosing = false;
		
		game.getNotification().info(ComputerPlayer.this + " chose card " + card);
		game.getNotification().warning(ComputerPlayer.this + " chose card " + card);
		
		game.nextMove(card);
	}
}
