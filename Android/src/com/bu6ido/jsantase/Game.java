/**
 * 
 */
package com.bu6ido.jsantase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bu6ido.jsantase.boards.ConsoleBoard;
import com.bu6ido.jsantase.boards.IBoard;
import com.bu6ido.jsantase.common.PlayCard;
import com.bu6ido.jsantase.common.SantaseSettings;
import com.bu6ido.jsantase.enums.Cards;
import com.bu6ido.jsantase.enums.Suits;
import com.bu6ido.jsantase.notifications.ConsoleNotification;
import com.bu6ido.jsantase.notifications.INotification;
import com.bu6ido.jsantase.players.AbstractPlayer;
import com.bu6ido.jsantase.players.ComputerPlayer;
import com.bu6ido.jsantase.players.ConsolePlayer;

/**
 * @author bu6ido
 *
 */
public class Game 
{
	public static final int FINAL_SCORE = 66;
	public static final int HALF_SCORE = 33;
	public static final int SCORE_PAIR = 20;
	public static final int SCORE_TRUMP_PAIR = 40;
	
	private boolean isInterrupted;
	private SantaseSettings settings;
	
	private boolean lastWon = false;
	
	private INotification notification;
	private IBoard board;
	
	private List<PlayCard> deck;
	private PlayCard trumpCard;
	
	private boolean isOne;
	
	private AbstractPlayer player1;
	private AbstractPlayer player2;
	
	private PlayCard oneCard;
	private PlayCard twoCard;
	
	private boolean isClosed;
	
	private AbstractPlayer closePlayer;
	
	public boolean getIsInterrupted()
	{
		return isInterrupted;
	}
	
	public void setIsInterrupted(boolean isInterrupted)
	{
		this.isInterrupted = isInterrupted;
	}
	
	public SantaseSettings getSettings()
	{
		return settings;
	}
	
	public void setSettings(SantaseSettings settings)
	{
		this.settings = settings;
	}
	
	public INotification getNotification()
	{
		return notification;
	}
	
	public void setNotification(INotification notification)
	{
		this.notification = notification;
	}
	
	public IBoard getBoard()
	{
		return board;
	}
	
	public void setBoard(IBoard board)
	{
		this.board = board;
	}
	
	// dali e pod ruka(igrae purvi)
	public boolean firstCardToDrop()
	{
		return (oneCard == null) && (twoCard == null);
	}
	
	// dali predi6niq igra4 e bil pod ruka(teku6tiq igrae vtori)
	public boolean firstCardDropped()
	{
		return (oneCard == null) || (twoCard == null);
	}
	
	public boolean isOne()
	{
		return isOne;
	}
	
	public AbstractPlayer getPlayer1()
	{
		return player1;
	}
	
	public void setPlayer1(AbstractPlayer player1)
	{
		this.player1 = player1;
	}
	
	public AbstractPlayer getPlayer2()
	{
		return player2;
	}
	
	public void setPlayer2(AbstractPlayer player2)
	{
		this.player2 = player2;
	}
	
	public PlayCard getOneCard()
	{
		return oneCard;
	}
	
	public PlayCard getTwoCard()
	{
		return twoCard;
	}
	
	public boolean deckHasCards()
	{
		return (deck != null) && (deck.size() > 0);
	}
	
	public boolean deckNotEnough()
	{
		return (deck.size() < 3);
	}
	
	protected void initDeck()
	{
		deck = new ArrayList<PlayCard>();
		for (Suits s : Suits.values())
		for (Cards c : Cards.values())
		{
			PlayCard card = new PlayCard(c, s);
			deck.add(card);
		}
		Collections.shuffle(deck);
	}
	
	public PlayCard removeFromDeck()
	{
		return deck.remove(0);
	}
	
	public void setTrumpCard(PlayCard trumpCard)
	{
		this.trumpCard = trumpCard;
	}
	
	public PlayCard getTrumpCard()
	{
		return trumpCard;
	}
	
	public Suits getTrump()
	{
		if (trumpCard != null)
		{
			return trumpCard.getSuit();
		}
		return null;
	}
	
	public boolean getIsClosed()
	{
		return isClosed;
	}
	
	public void setIsClosed(boolean isClosed)
	{
		this.isClosed = isClosed;
	}
	
	public void closeGame(AbstractPlayer player)
	{
		if (deckNotEnough())
		{
			notification.warning("You cannot close - only " + deck.size() + " cards left in the deck !!!");
			return;
		}
		if (!firstCardToDrop())
		{
			notification.warning("You cannot close - you are not in turn to play the first card !!!");
			return;
		}
		setIsClosed(true);
		closePlayer = player;
		board.print();
		notification.info(player + " closed the game.");
		notification.warning(player + " closed the game.");
	}
	
	public void changeTrump(AbstractPlayer player)
	{
		// player is not the one that dropped the first card
		if (!firstCardToDrop())
		{
			notification.warning("You cannot change trump - you're not in turn to play the first card !!!");
			return;
		}
		// the trump card is NINE - no point to change
		if (trumpCard.getCard().val() == Cards.NINE.val())
		{
			notification.warning("You cannot change trump, because it is NINE !!!");
			return;
		}
		// less than 3 cards left in the deck - the change is forbidden
		if (deckNotEnough())
		{
			notification.warning("You cannot change trump - only " + deck.size() + " left in the deck !!!");
			return;
		}
		// check if there is NINE from the trump in the player cards
		PlayCard nineTrump = null;
		for (Iterator<PlayCard> iter = player.getCards().iterator(); iter.hasNext(); )
		{
			PlayCard card = iter.next();
			if ((card.getCard().val() == Cards.NINE.val()) &&
				(card.getSuit().val() == trumpCard.getSuit().val()) )
			{
				nineTrump = card;
				break;
			}
		}
		// no NINE from trump in his cards
		if (nineTrump == null)
		{
			nineTrump = new PlayCard(Cards.NINE, trumpCard.getSuit());
			notification.warning("You cannot change trump - you don't have " + nineTrump.toString() + " !!!");
			return;
		}
		
		// everything's fine - do the change
		player.removeCard(nineTrump); // remove nine from player's cards
		player.addCard(trumpCard); // adds the old trump card to player's
		deck.set(deck.size() - 1, nineTrump); // change the trump card in the bottom of the deck
		setTrumpCard(nineTrump);
		Collections.sort(player.getCards());
		printStatus();
		board.print();
	}
	
	public void startNewGame()
	{
		setIsInterrupted(false);
		if (settings != null)
		{
			player1.setName(settings.getNamePlayer1());
			player2.setName(settings.getNamePlayer2());
		}
	
		notification.info("Starting new game.");
		initDeck();
		player1.clearData();
		player2.clearData();
		
		for (int i=0; i<6; i++)
		{
			PlayCard card = removeFromDeck();
			player1.addCard(card);
			card = removeFromDeck();
			player2.addCard(card);
		}
		setTrumpCard(deck.get(deck.size() - 1));
		setIsClosed(false);
		closePlayer = null;
		oneCard = null;
		twoCard = null;
		printStatus();
		isOne = !lastWon;
		if (isOne)
		{
			player1.play();
		}
		else
		{
			player2.play();
		}
	}
	
	public void printStatus()
	{
		StringBuilder sb = new StringBuilder();
		if (isClosed)
		{
			sb.append("Closed/deck empty");
		}
		else
		{
			sb.append("Cards in deck: " + deck.size());
		}
		sb.append(", Trump card: " + trumpCard);
		sb.append(", " + player1 + " score: " + player1.getScore());
		sb.append(", " + player2 + " score: " + player2.getScore());
		notification.status(sb.toString());
	}

	public boolean validateCard(AbstractPlayer player, PlayCard card, boolean showWarnings)
	{
		if (card == null)
		{
			return false;
		}
		// if it's closed and first card dropped
		// this player must answer with a card of the same suit or trump
		if (isClosed && firstCardDropped())
		{
			if (isOne && (twoCard != null))
			{
				if (card.getSuit().val() != twoCard.getSuit().val())
				{
					for (Iterator<PlayCard> iter = player1.getCards().iterator(); iter.hasNext(); )
					{
						PlayCard workCard = iter.next();
						if (workCard.getSuit().val() == twoCard.getSuit().val())
						{
							if (showWarnings)
							{
								notification.warning("Since the deck is empty or the game is closed," +
										"\nyou should put a card with the same suit as the opponent one !!!");
							}
							return false;
						}
					}
					
					if (card.getSuit().val() != getTrump().val())
					{
						for (Iterator<PlayCard> iter = player1.getCards().iterator(); iter.hasNext(); )
						{
							PlayCard workCard = iter.next();
							if (workCard.getSuit().val() == getTrump().val())
							{
								if (showWarnings)
								{
									notification.warning("Since the deck is empty or the game is closed, you must put a trump !!!");
								}
								return false;
							}
						}
					}
				}
				else
				// check if the raising of the same suit is obligatory
				if (card.getCard().val() < twoCard.getCard().val())
				{
					if ((settings != null) && settings.getRaising())
					for (Iterator<PlayCard> iter = player1.getCards().iterator(); iter.hasNext(); )
					{
						PlayCard workCard = iter.next();
						if ((workCard.getSuit().val() == twoCard.getSuit().val()) &&
							(workCard.getCard().val() > twoCard.getCard().val()) )
						{
							if (showWarnings)
							{
								notification.warning("Since the deck is empty or the game is closed, \n" + 
										"you must raise with a stronger card of the same suit !!!");
							}
							return false;
						}
					}
				}
			}
			else
			if (!isOne && (oneCard != null))
			{
				if (card.getSuit().val() != oneCard.getSuit().val())
				{
					for (Iterator<PlayCard> iter = player2.getCards().iterator(); iter.hasNext(); )
					{
						PlayCard workCard = iter.next();
						if (workCard.getSuit().val() == oneCard.getSuit().val())
						{
							if (showWarnings)
							{
								notification.warning("Since the deck is empty or the game is closed," +
										"\nyou should put a card with the same suit as the opponent one !!!");
							}
							return false;
						}
					}
					
					if (card.getSuit().val() != getTrump().val())
					{
						for (Iterator<PlayCard> iter = player2.getCards().iterator(); iter.hasNext(); )
						{
							PlayCard workCard = iter.next();
							if (workCard.getSuit().val() == getTrump().val())
							{
								if (showWarnings)
								{
									notification.warning("Since the deck is empty or the game is closed, you must put a trump !!!");
								}
								return false;
							}
						}
					}
				}
				else
				// check if the raising of the same suit is obligatory
				if (card.getCard().val() < oneCard.getCard().val())
				{
					if ((settings != null) && settings.getRaising())
					for (Iterator<PlayCard> iter = player2.getCards().iterator(); iter.hasNext(); )
					{
						PlayCard workCard = iter.next();
						if ((workCard.getSuit().val() == oneCard.getSuit().val()) &&
							(workCard.getCard().val() > oneCard.getCard().val()) )
						{
							if (showWarnings)
							{
								notification.warning("Since the deck is empty or the game is closed, \n" + 
										"you must raise with a stronger card of the same suit !!!");
							}
							return false;
						}
					}
				}				
			}
		}

		// only if it's the first card to drop
		if (firstCardToDrop())
		{
			// if playing with queen - checks for 20 or 40
			if (card.getCard().val() == Cards.QUEEN.val())
			{
				boolean kingFound = false;
				for (Iterator<PlayCard> iter = player.getCards().iterator(); iter.hasNext(); )
				{
					PlayCard workCard = iter.next();
					if ((workCard.getSuit().val() == card.getSuit().val()) &&
						(workCard.getCard().val() == Cards.KING.val()) )
					{
						kingFound = true;
						break;
					}
				}
				if (kingFound)
				{
					if (showWarnings)
					{
						player.addPair(card.getSuit());
						int score = SCORE_PAIR;
						if (card.getSuit().val() == getTrump().val())
						{
							score = SCORE_TRUMP_PAIR;
						}
						notification.info(player + " announced " + score);
						notification.warning(player + " announced " + score);
					}
				}
			}
		}
		return true;
	}
	
	public void nextMove(PlayCard card)
	{
		if (checkGameOver())
		{
			return;
		}
		if (deck.size() == 0)
		{
			setIsClosed(true);
		}
		// removes the chosen card from current cards of player
		// and stores it in oneCard or twoCard
		if (isOne)
		{
			player1.removeCard(card);
			oneCard = card;
		}
		else
		{
			player2.removeCard(card);
			twoCard = card;
		}
		
		// if both oneCard and twoCard set
		if ((oneCard != null) && (twoCard != null))
		{
			// check if player1 has better card
			boolean better = firstCardBetter(oneCard, twoCard);
			notification.info(oneCard + (better? " > " : " < ") + twoCard);
			board.fireTakeCards(better);
			
			if (better)
			{
				notification.info(player1 + " takes the cards.");
				// player1 takes the cards
				player1.addTakenCard(oneCard);
				player1.addTakenCard(twoCard);
				
				// both players get new card from the deck
				// player1 first
				if ((deck.size() > 0) && !isClosed)
				{
					PlayCard newCard = removeFromDeck();
					player1.addCard(newCard);
					newCard = removeFromDeck();
					player2.addCard(newCard);
				}
				else
				{
					setIsClosed(true);
				}
			}
			else
			{
				notification.info(player2 + " takes the cards.");
				// player2 takes the cards
				player2.addTakenCard(oneCard);
				player2.addTakenCard(twoCard);
				
				// both players get new card from the deck
				// player2 first
				if ((deck.size() > 0) && !isClosed)
				{
					PlayCard newCard = removeFromDeck();
					player2.addCard(newCard);
					newCard = removeFromDeck();
					player1.addCard(newCard);
				}
				else
				{
					setIsClosed(true);
				}
			}
			
			// clears variables
			oneCard = null;
			twoCard = null;
		
		    // next player is the same with better card
		    isOne = better;
		}
		else
		{
			// switch to the other player
			isOne = !isOne;
		}

		if (checkGameOver())
		{
			return;
		}
		if (deck.size() == 0)
		{
			setIsClosed(true);
		}
		printStatus();
		
		// forces other player to play
		if (isOne)
		{
			player1.play();
		}
		else
		{
			player2.play();
		}
	}
	
	// checks if first player has a stronger card then the second
	protected boolean firstCardBetter(PlayCard first, PlayCard second)
	{
		// different suits
		if (first.getSuit().val() != second.getSuit().val())
		{
			// first card is trump, second - not
			if (first.getSuit().val() == trumpCard.getSuit().val())
			{
				return true;
			}
			else
			// second card is trump, first - not
			if (second.getSuit().val() == trumpCard.getSuit().val())
			{
				return false;
			}
			// both are no trumps, whoever played the first card is stronger
			return !isOne;
		}
		else
		{
			// equal suits - compare the cards kind
			if (first.getCard().val() >= second.getCard().val())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	protected boolean checkGameOver()
	{
		if (getIsInterrupted())
		{
			return true;
		}
		int score1 = player1.getScore();
		int score2 = player2.getScore();
		
		int winPoints = 0;
		
		if (score1 >= FINAL_SCORE)
		{
			if ((closePlayer != null) && closePlayer.equals(player2))
			{
				winPoints = 3;
			}
			else
			{
				if (score2 <= 0)
				{
					winPoints = 3;
				}
				else
				if (score2 < HALF_SCORE)
				{
					winPoints = 2;
				}
				else
				{
					winPoints = 1;
				}
			}
			printStatus();
			lastWon = true;
			notification.info("Game over: " + player1 + " wins: " + winPoints + " points.");
			notification.warning("Game over: " + player1 + " wins: " + winPoints + " points.");
			return true;
		}
		else
		if (score2 >= FINAL_SCORE)
		{
			if ((closePlayer != null) && closePlayer.equals(player1))
			{
				winPoints = 3;
			}
			else
			{
				if (score1 <= 0)
				{
					winPoints = 3;
				}
				else
				if (score1 < HALF_SCORE)
				{
					winPoints = 2;
				}
				else
				{
					winPoints = 1;
				}
			}
			printStatus();
			lastWon = false;
			notification.info("Game over: " + player2 + " wins: " + winPoints + " points.");
			notification.warning("Game over: " + player2 + " wins: " + winPoints + " points.");
			return true;
		}
		if ((player1.getCards().size() == 0) && (player2.getCards().size() == 0))
		{
			printStatus();
			if ((closePlayer != null) && closePlayer.equals(player2))
			{
				lastWon = true;
				notification.info("Game over: " + player1 + " wins: 3 points.");
				notification.warning("Game over: " + player1 + " wins: 3 points.");
			}
			else
			if ((closePlayer != null) && closePlayer.equals(player1))
			{
				lastWon = false;
				notification.info("Game over: " + player2 + " wins: 3 points.");
				notification.warning("Game over: " + player2 + " wins: 3 points.");
			}
			else
			if (score1 >= score2)
			{
				if (score2 <= 0)
				{
					winPoints = 3;
				}
				else
				if (score2 < HALF_SCORE)
				{
					winPoints = 2;
				}
				else
				{
					winPoints = 1;
				}
				lastWon = true;
				notification.info("Game over: " + player1 + " wins: " + winPoints + " points.");
				notification.warning("Game over: " + player1 + " wins: " + winPoints + " points.");
			}
			else
			{
				if (score1 <= 0)
				{
					winPoints = 3;
				}
				else
				if (score1 < HALF_SCORE)
				{
					winPoints = 2;
				}
				else
				{
					winPoints = 1;
				}
				lastWon = false;
				notification.info("Game over: " + player2 + " wins: " + winPoints + " points.");
				notification.warning("Game over: " + player2 + " wins: " + winPoints + " points.");
			}
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) 
	{
		Game game = new Game();
		ConsoleNotification notification = new ConsoleNotification();
		ConsoleBoard board = new ConsoleBoard(game);
		ConsolePlayer player1 = new ConsolePlayer(game, "First");
		ComputerPlayer player2 = new ComputerPlayer(game, "Second");
		game.setNotification(notification);
		game.setBoard(board);
		game.setPlayer1(player1);
		game.setPlayer2(player2);
		game.startNewGame();
	}
}
