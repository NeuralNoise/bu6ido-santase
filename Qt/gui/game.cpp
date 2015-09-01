#include "game.h"

Game::Game()
{
//  cout << "Constructing Game" << endl;
  oneCard = NULL;
  twoCard = NULL;
  lastWon = false;
  initCards();
}

Game::~Game()
{
  freeCards();
//  cout << "Destroying Game" << endl;
}

bool Game::getIsInterrupted()
{
  return isInterrupted;
}

void Game::setIsInterrupted(bool isInterrupted)
{
  this->isInterrupted = isInterrupted;
}

void Game::initCards()
{
  Suits allSuits[] = { CLUB, DIAMOND, HEART, SPADE };
  Cards allCards[] = { NINE, JACK, QUEEN, KING, TEN, ACE };
  int countSuits = sizeof(allSuits) / sizeof(Suits);
  int countCards = sizeof(allCards) / sizeof(Cards);

  for (int i=0; i<countSuits; i++)
  for (int j=0; j<countCards; j++)
  {
    Suits s = allSuits[i];
    Cards c = allCards[j];
    Card *card = new Card(c, s);
    cards.push_back(card);
  }
}

void Game::freeCards()
{
  for (int i=0; i<cards.size(); i++)
  {
    Card *card = cards.at(i);
    delete card;
  }
}

void Game::initDeck()
{
  deck = cards;
  srand(time(0));
  random_shuffle(deck.begin(), deck.end());
}

IBoard * Game::getBoard()
{
  return board;
}

void Game::setBoard(IBoard *board)
{
  this->board = board;
}

INotification * Game::getNotification()
{
  return notification;
}

void Game::setNotification(INotification *notification)
{
  this->notification = notification;
}

AbstractPlayer * Game::getPlayer1()
{
  return player1;
}

void Game::setPlayer1(AbstractPlayer *player1)
{
  this->player1 = player1;
}

AbstractPlayer * Game::getPlayer2()
{
  return player2;
}

void Game::setPlayer2(AbstractPlayer *player2)
{
  this->player2 = player2;
}

bool Game::firstCardToDrop()
{
  return !oneCard && !twoCard;
}

bool Game::firstCardDropped()
{
  return (oneCard && !twoCard) || (!oneCard && twoCard);
}

Card * Game::getOneCard()
{
  return oneCard;
}

Card * Game::getTwoCard()
{
  return twoCard;
}

Card * Game::getTrumpCard()
{
  return trumpCard;
}

void Game::setTrumpCard(Card *trumpCard)
{
  this->trumpCard = trumpCard;
}

Suits Game::getTrump()
{
  if (trumpCard)
  {
    return trumpCard->getSuit();
  }
  return CLUB;
}

bool Game::getIsOne()
{
  return isOne;
}

bool Game::deckHasCards()
{
  return !deck.empty();
}

bool Game::deckNotEnough()
{
  return deck.size() < 3;
}

Card * Game::removeFromDeck()
{
  Card *card = deck.front();
  removeItem(deck, card);
  return card;
}

bool Game::getIsClosed()
{
  return isClosed;
}

void Game::setIsClosed(bool isClosed)
{
  this->isClosed = isClosed;
}

void Game::closeGame(AbstractPlayer *player)
{
  stringstream ss;
  if (deckNotEnough())
  {
    ss.str("");
    ss << "You cannot close - only " << deck.size() << " cards left in the deck !!!";
    notification->warning(ss.str());
    return;
  }
  if (!firstCardToDrop())
  {
    notification->warning("You cannot close - you are not in turn to play the first card !!!");
    return;
  }
  setIsClosed(true);
  closePlayer = player;
  board->print();
  ss.str("");
  ss << *player << " closed the game.";
  notification->info(ss.str());
  notification->warning(ss.str());
}

void Game::changeTrump(AbstractPlayer *player)
{
  stringstream ss;
  // player is not the one that dropped the first card
  if (!firstCardToDrop())
  {
    notification->warning("You cannot change trump - you're not in turn to play the first card !!!");
    return;
  }
  // the trump card is NINE - no point to change
  if (trumpCard->getCard() == NINE)
  {
    notification->warning("You cannot change trump, because it is NINE !!!");
    return;
  }
  // less than 3 cards left in the deck - the change is forbidden
  if (deckNotEnough())
  {
    ss.str("");
    ss << "You cannot change trump - only " << deck.size() << " left in the deck !!!";
    notification->warning(ss.str());
    return;
  }
  // check if there is NINE from the trump in the player cards
  Card *nineTrump = NULL;
  for (int i=0; i<player->getCards().size(); i++)
  {
    Card *card = player->getCards().at(i);
    if ((card->getCard() == NINE) &&
	(card->getSuit() == trumpCard->getSuit()) )
    {
      nineTrump = card;
      break;
    }
  }
  // no NINE from trump in his cards
  if (!nineTrump)
  {
    nineTrump = new Card(NINE, trumpCard->getSuit());
    ss.str("");
    ss << "You cannot change trump - you don't have " << *nineTrump << " !!!";
    notification->warning(ss.str());
    delete nineTrump;
    return;
  }
		
  // everything's fine - do the change
  player->removeCard(nineTrump); // remove nine from player's cards
  player->addCard(trumpCard); // adds the old trump card to player's
  deck.pop_back();
  deck.push_back(nineTrump); // change the trump card in the bottom of the deck
  setTrumpCard(nineTrump);
  sort(player->getCards().begin(), player->getCards().end(), cmpCard);
  printStatus();
  board->print();
}

void Game::printStatus()
{
  stringstream ss;
  if (isClosed)
  {
    ss << "Closed/deck empty";
  }
  else
  {
    ss << "Cards in deck: " << deck.size();
  }
  ss << ", Trump card: " << *trumpCard;
  ss << ", " << *player1 << " score: " << player1->getScore();
  ss << ", " << *player2 << " score: " << player2->getScore();
  notification->status(ss.str());
}

bool Game::validateCard(AbstractPlayer *player, Card *card, bool showWarnings)
{
  stringstream ss;
  if (!card)
  {
    return false;
  }
  // if it's closed and first card dropped
  // this player must answer with a card of the same suit or trump
  if (isClosed && firstCardDropped())
  {
    if (isOne && twoCard)
    {
      if (card->getSuit() != twoCard->getSuit())
      {
        for (int i=0; i<player1->getCards().size(); i++)
        {
          Card *workCard = player1->getCards().at(i);
  	  if (workCard->getSuit() == twoCard->getSuit())
          {
            if (showWarnings)
	    {
              ss.str("");
              ss << "Since the deck is empty or the game is closed," << 
                    "\nyou should put a card with the same suit as the opponent one !!!";
              notification->warning(ss.str());
	    }
	    return false;
	  }
        }
					
	if (card->getSuit() != getTrump())
	{
          for (int i=0; i<player1->getCards().size(); i++)
	  {
	    Card *workCard = player1->getCards().at(i);
	    if (workCard->getSuit() == getTrump())
            {
	      if (showWarnings)
	      {
		notification->warning("Since the deck is empty or the game is closed, you must put a trump !!!");
	      }
	      return false;
	    }
	  }
	}
      }
      else
      // check if the raising of the same suit is obligatory
      if (card->getCard() < twoCard->getCard())
      {
        if (settings.getIsRaising())
        {
          for (int i=0; i<player1->getCards().size(); i++)
          {
            Card *workCard = player1->getCards().at(i);
            if ((workCard->getSuit() == twoCard->getSuit()) &&
                (workCard->getCard() > twoCard->getCard()) )
            {
              if (showWarnings)
              {
                ss.str("");
                ss << "Since the deck is empty or the game is closed, " << endl;
                ss << "you must raise with a stronger card of the same suit !!!";
                notification->warning(ss.str());
              }
              return false;
            }
          }
        }
      }
    }
    else
    if (!isOne && oneCard)
    {
      if (card->getSuit() != oneCard->getSuit())
      {
        for (int i=0; i<player2->getCards().size(); i++)
        {
          Card *workCard = player2->getCards().at(i);
	  if (workCard->getSuit() == oneCard->getSuit())
	  {
	    if (showWarnings)
	    {
              ss.str("");
              ss << "Since the deck is empty or the game is closed," << 
                    "\nyou should put a card with the same suit as the opponent one !!!";
              notification->warning(ss.str());
	    }
            return false;
	  }
        }
					
        if (card->getSuit() != getTrump())
        {
          for (int i=0; i<player2->getCards().size(); i++)
          {
            Card *workCard = player2->getCards().at(i);
	    if (workCard->getSuit() == getTrump())
	    {
              if (showWarnings)
              {
                notification->warning("Since the deck is empty or the game is closed, you must put a trump !!!");
              }
              return false;
            }
          }
        }
      }
      else
      // check if the raising of the same suit is obligatory
      if (card->getCard() < oneCard->getCard())
      {
        if (settings.getIsRaising())
        {
          for (int i=0; i<player2->getCards().size(); i++)
          {
            Card *workCard = player2->getCards().at(i);
            if ((workCard->getSuit() == oneCard->getSuit()) &&
                (workCard->getCard() > oneCard->getCard()) )
            { 
              if (showWarnings)
              {
                ss.str("");
                ss << "Since the deck is empty or the game is closed, " << endl;
                ss << "you must raise with a stronger card of the same suit !!!";
                notification->warning(ss.str());
              }
              return false;
            }
          }
        }
      }
    }
  }

  // only if it's the first card to drop
  if (firstCardToDrop())
  {
    // if playing with queen - checks for 20 or 40
    if (card->getCard() == QUEEN)
    {
      bool kingFound = false;
      for (int i=0; i<player->getCards().size(); i++)
      {
        Card * workCard = player->getCards().at(i);
        if ((workCard->getSuit() == card->getSuit()) &&
	    (workCard->getCard() == KING) )
        {
          kingFound = true;
          break;
        }
      }
      if (kingFound)
      {
        if (showWarnings)
        {
          player->addPair(card->getSuit());
          int score = SCORE_PAIR;
          if (card->getSuit() == getTrump())
          {
            score = SCORE_TRUMP_PAIR;
          }
          ss.str("");
          ss << *player << " announced " << score;
          notification->info(ss.str());
	  notification->warning(ss.str());
        }
      }
    }
  }
  return true;
}

bool Game::checkGameOver()
{
  if (getIsInterrupted())
  {
    return true;
  }
  stringstream ss;
  int score1 = player1->getScore();
  int score2 = player2->getScore();

  int winPoints = 0;

  if (score1 >= FINAL_SCORE)
  {
    if (closePlayer && (closePlayer == player2))
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
    board->fireResetMoves();
    lastWon = true;
    ss.str("");
    ss << "Game over: " << *player1 << " wins: " << winPoints << " points.";
    notification->info(ss.str());
    notification->warning(ss.str());
    return true;
  }
  else
  if (score2 >= FINAL_SCORE)
  {
    if (closePlayer && (closePlayer == player1))
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
    board->fireResetMoves();
    lastWon = false;
    ss.str("");
    ss << "Game over: " << *player2 << " wins: " << winPoints << " points.";
    notification->info(ss.str());
    notification->warning(ss.str());
    return true;
  }

  
  if (player1->getCards().empty() && player2->getCards().empty())
  {
    printStatus();
    board->fireResetMoves();

    if (closePlayer && (closePlayer == player2))
    {
      lastWon = true;
      ss.str("");
      ss << "Game over: " << *player1 << " wins: 3 points.";
      notification->info(ss.str());
      notification->warning(ss.str());
    }
    else
    if (closePlayer && (closePlayer == player1))
    {
      lastWon = false;
      ss.str("");
      ss << "Game over: " << *player2 << " wins: 3 points.";
      notification->info(ss.str());
      notification->warning(ss.str());
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
      ss.str("");
      ss << "Game over: " << *player1 << " wins: " << winPoints << " points.";
      notification->info(ss.str());
      notification->warning(ss.str());
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
      ss.str("");
      ss << "Game over: " << *player2 << " wins: " << winPoints << " points.";
      notification->info(ss.str());
      notification->warning(ss.str());
    }
    return true;
  }

  return false;
}

void Game::startNewGame()
{
  setIsInterrupted(false);
  if (!player1 || !player2) return;

  loadSettings();
  player1->setName(settings.getNamePlayer1());
  player2->setName(settings.getNamePlayer2());

  notification->info("Starting new game.");
  initDeck();
  player1->clearCards();
  player2->clearCards();
  for (int i=0; i<CARDS_IN_HAND; i++)
  {
    Card *card = removeFromDeck();
    player1->addCard(card);
    card = removeFromDeck();
    player2->addCard(card);
  }
  setTrumpCard(deck.back());
  setIsClosed(false);
  closePlayer = NULL;
  oneCard = NULL;
  twoCard = NULL;
  printStatus();
  isOne = !lastWon;
  if (isOne)
  {
    player1->play();
  }
  else
  {
    player2->play();
  }
}

bool Game::firstCardBetter(Card *first, Card *second)
{
  // different suits
  if (first->getSuit() != second->getSuit())
  {
    // first card is trump, second - not
    if (first->getSuit() == getTrump())
    {
      return true;
    }
    else
    // second card is trump, first - not
    if (second->getSuit() == getTrump())
    {
      return false;
    }
    // both are no trumps, whoever played the first card is stronger
    return !isOne;
  }
  else
  {
    // equal suits - compare the cards kind
    return first->getCard() >= second->getCard();
  }
}

void Game::nextMove(Card *card)
{
  stringstream ss;
  if (checkGameOver())
  {
    return;
  }
  if (!deckHasCards())
  {
    setIsClosed(true);
  }
  // removes the chosen card from current cards of player
  // and stores it in oneCard or twoCard
  if (isOne)
  {
    player1->removeCard(card);
    oneCard = card;
  }
  else
  {
    player2->removeCard(card);
    twoCard = card;
  }

  // if both oneCard and twoCard set
  if (oneCard && twoCard)
  {
    // check if player1 has better card
    bool better = firstCardBetter(oneCard, twoCard);
    ss.str("");
    ss << *oneCard << (better? " > " : " < ") << *twoCard;
    notification->info(ss.str());
    board->fireTakeCards(better);

    if (better)
    {
      ss.str("");
      ss << *player1 << " takes the cards.";
      notification->info(ss.str());
      // player1 takes the cards
      player1->addTakenCard(oneCard);
      player1->addTakenCard(twoCard);

      // both players get new card from the deck
      // player1 first
      if (deckHasCards() && !isClosed)
      {
        Card *newCard = removeFromDeck();
        player1->addCard(newCard);
        newCard = removeFromDeck();
        player2->addCard(newCard);
      }
      else
      {
        setIsClosed(true);
      }
    }
    else
    {
      ss.str("");
      ss << *player2 << " takes the cards.";
      notification->info(ss.str());
      // player2 takes the cards
      player2->addTakenCard(oneCard);
      player2->addTakenCard(twoCard);

      // both players get new card from the deck
      // player2 first
      if (deckHasCards() && !isClosed)
      {
        Card *newCard = removeFromDeck();
        player2->addCard(newCard);
        newCard = removeFromDeck();
        player1->addCard(newCard);
      }
      else
      {
        setIsClosed(true);
      }
    }

    // clears variables
    oneCard = NULL;
    twoCard = NULL;

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
  if (!deckHasCards())
  {
    setIsClosed(true);
  }
  printStatus();

  // forces other player to play
  if (isOne)
  {
    player1->play();
  }
  else
  {
    player2->play();
  }
}

void Game::loadSettings()
{
  QFile file("qsantase.dat");
  bool ok = file.open(QIODevice::ReadOnly);
  if (ok)
  {
    QDataStream stream(&file);
    stream >> settings;  
  }
}

