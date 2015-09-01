#include "computer_player.h"
#include "card.h"

ComputerPlayer::ComputerPlayer(Game *game, string name) : AbstractPlayer(game, name)
{
//  cout << "Constructing ComputerPlayer" << endl;
}

ComputerPlayer::~ComputerPlayer()
{
//  cout << "Destroying ComputerPlayer" << endl;
}

void ComputerPlayer::checkForTrumpCardChange()
{
  if (game->getIsClosed())
  {
    return;
  }
  if (!game->firstCardToDrop())
  {
    return;
  }
  if (game->getTrumpCard() && (game->getTrumpCard()->getCard() == NINE))
  {
    return;
  }
  if (game->deckNotEnough())
  {
    return;
  }
  // check if there is NINE from the trump in the player cards
  Card *nineTrump = NULL;
  for (int i=0; i<cards.size(); i++)
  {
    Card *workCard = cards.at(i);
    if ( (workCard->getCard() == NINE) && 
         (workCard->getSuit() == game->getTrump()) )
    {
      nineTrump = workCard;
      break;
    }
  }
  if (!nineTrump)
  {
    return;
  }
  game->changeTrump(this);
  stringstream ss;
  ss.str("");
  ss << *this << " changed the trump card.";
  game->getNotification()->info(ss.str());
  game->getNotification()->warning(ss.str());
}

void ComputerPlayer::checkForGameClose()
{
  if (game->getIsClosed())
  {
    return;
  }
  if (game->deckNotEnough())
  {
    return;
  }
  if (!game->firstCardToDrop())
  {
    return;
  }
  int currentScore = getScore();
  
  AbstractPlayer *opponent = (this == game->getPlayer1())? game->getPlayer2() : game->getPlayer1();

  vector<Card *> trumpCards;
  int myTrumpScore = 0;
  for (int i=0; i<cards.size(); i++)
  {
    Card *workCard = cards.at(i);
    if (workCard->getSuit() == game->getTrump())
    {
      trumpCards.push_back(workCard);
      myTrumpScore += workCard->getCard();
    }
  }

  vector<Card *> oppTrumpCards;
  int oppTrumpScore = 0;
  for (int i=0; i<opponent->getCards().size(); i++)
  {
    Card *workCard = opponent->getCards().at(i);
    if (workCard->getSuit() == game->getTrump())
    {
      oppTrumpCards.push_back(workCard);
      oppTrumpScore += workCard->getCard();
    }
  }

  if (myTrumpScore <= oppTrumpScore)
  {
    return;
  }

  int myStrongScore = 0;
  for (int i=0; i<cards.size(); i++)
  {
    Card *workCard = cards.at(i);
    if ((workCard->getSuit() != game->getTrump()) &&
       ((workCard->getCard() == ACE) || (workCard->getCard() == TEN)) )
    {
      myStrongScore += workCard->getCard();
    }
  }

  int oppStrongScore = 0;
  for (int i=0; i<opponent->getCards().size(); i++)
  {
    Card *workCard = opponent->getCards().at(i);
    if ((workCard->getSuit() != game->getTrump()) &&
       ((workCard->getCard() == ACE) || (workCard->getCard() == TEN)) )
    {
      oppStrongScore += workCard->getCard();
    }
  }

  if (myStrongScore < oppStrongScore)
  {
    return;
  }

  if ((currentScore + myTrumpScore + myStrongScore + TEN) < FINAL_SCORE)
  {
    return;
  }

  game->closeGame(this);
}

void ComputerPlayer::play()
{
  AbstractPlayer::play();
  stringstream ss;

  isChoosing = true;

  ss << *this << " is thinking ...";
  game->getNotification()->info(ss.str());

  checkForTrumpCardChange();
  checkForGameClose();

  card = NULL;
  vector<Card *> availCards;
  for (int i=0; i<cards.size(); i++)
  {
    Card *workCard = cards.at(i);
    if (game->validateCard(this, workCard, false))
    {
      availCards.push_back(workCard);
    }
  }
  if (availCards.size() == 1)
  {
    card = availCards.front();
  }
  else
  {
    // 4 different strategies depending on if this player is first or second
    // and if the game is closed or not
    AbstractPlayer *opponent = (this == game->getPlayer1())? game->getPlayer2() : game->getPlayer1();

    if (game->firstCardToDrop())
    {
      if (game->getIsClosed())
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

        Suits suits[] = { CLUB, DIAMOND, HEART, SPADE };
        int cSuit = sizeof(suits) / sizeof(Suits);
        for (int i=0; i<cSuit; i++)
        {
          Suits workSuit = suits[i];

          int max = 0;
          Card *maxCard = NULL;
          for(int j=0; j<availCards.size(); j++)
          {
            Card *workCard = availCards.at(j);
            if (workCard->getSuit() == workSuit)
            {
              if (workCard->getCard() > max)
              {
                max = workCard->getCard();
                maxCard = workCard;
              }
            }
          }

          if (maxCard)
          {
            bool hasStronger = false;
            bool countSuit = 0;
            for (int j=0; j<opponent->getCards().size(); j++)
            {
              Card *workCard = opponent->getCards().at(j);
              if (workCard->getSuit() == workSuit)
              {
                countSuit++;
                if (workCard->getCard() > maxCard->getCard())
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

        if (!card)
        {
          // checks for 20 or 40 and play the queen
          vector<Card *> queens;
          vector<int> scores;
          for (int i=0; i<availCards.size(); i++)
          {
            Card *workCard = availCards.at(i);
            if (workCard->getCard() == QUEEN)
            {
              for (int j=0; j<availCards.size(); j++)
              {
                Card *workCard2 = availCards.at(j);
                if ((workCard->getSuit() == workCard2->getSuit()) &&
                    (workCard2->getCard() == KING) )
                {
                  queens.push_back(workCard);
                  if (workCard2->getSuit() == game->getTrump())
                  {
                    scores.push_back(SCORE_TRUMP_PAIR);
                  }
                  else
                  {
                    scores.push_back(SCORE_PAIR);
                  }
                }
              }
            }
          }
          int max = 0;
          for (int i=0; i<min(queens.size(), scores.size()); i++)
          {
            int sc = scores.at(i);
            if (max < sc)
            {
              max = sc;
              card = queens.at(i);
            }
          }
        }

        if (!card)
        {
          // suits of cards that the opponent has
          vector<Suits> hasSuits;
          for (int i=0; i<opponent->getCards().size(); i++)
          {
            Card *workCard = opponent->getCards().at(i);
            if (!vecContains(hasSuits, workCard->getSuit()))
            {
              hasSuits.push_back(workCard->getSuit());
            }
          }

          Card *weakest = NULL;
          for (int i=0; i<availCards.size(); i++)
          {
            Card *workCard = availCards.at(i);
            if (!vecContains(hasSuits, workCard->getSuit()))
            {
              if (!weakest)
              {
                weakest = workCard;
              }
              if (workCard->getCard() < weakest->getCard())
              {
                weakest = workCard;
              }
            }
          }

          card = weakest;
        }

        if (!card)
        {
          vector<Card *> trumpCards;
          for (int i=0; i<availCards.size(); i++)
          {
            Card *workCard = availCards.at(i);
            if (workCard->getSuit() == game->getTrump())
            {
              trumpCards.push_back(workCard);
            }
          }
          sort(trumpCards.begin(), trumpCards.end(), cmpCard);

          vector<Card *> oppTrumpCards;
          for (int i=0; i<opponent->getCards().size(); i++)
          {
            Card *workCard = opponent->getCards().at(i);
            if (workCard->getSuit() == game->getTrump())
            {
              oppTrumpCards.push_back(workCard);
            }
          }
          sort(oppTrumpCards.begin(), oppTrumpCards.end(), cmpCard);

          if ((oppTrumpCards.size() < 2) && !trumpCards.empty())
          { 
            card = trumpCards.front();
          }
          else
          if (oppTrumpCards.size() >= 2)
          {
            Card *firstTrump = oppTrumpCards.back();
            Card *secondTrump = oppTrumpCards.at(oppTrumpCards.size() - 2);

            vector<Card *> weakerCards;
            for (int i=0; i<trumpCards.size(); i++)
            {
              Card *workCard = trumpCards.at(i);
              if ((workCard->getCard() > secondTrump->getCard()) &&
                  (workCard->getCard() < firstTrump->getCard()) )
              {
                weakerCards.push_back(workCard);
              }
            }
            sort(weakerCards.begin(), weakerCards.end(), cmpCard);

            if (!weakerCards.empty())
            {
              card = weakerCards.front();
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
        vector<Card *> queens;
        vector<int> scores;
        for (int i=0; i<availCards.size(); i++)
        {
          Card *workCard = availCards.at(i);
          if (workCard->getCard() == QUEEN)
          {
            for (int j=0; j<availCards.size(); j++)
            {
              Card *workCard2 = availCards.at(j);
              if ((workCard->getSuit() == workCard2->getSuit()) &&
                  (workCard2->getCard() == KING) )
              {
                queens.push_back(workCard);
                if (workCard2->getSuit() == game->getTrump())
                {
                  scores.push_back(SCORE_TRUMP_PAIR);
                }
                else
                {
                  scores.push_back(SCORE_PAIR);
                }
              }
            }
          }
        }
        int max = 0;
        for (int i=0; i<min(queens.size(), scores.size()); i++)
        {
          int sc = scores.at(i);
          if (max < sc)
          {
            max = sc;
            card = queens.at(i);
          }
        }

        // no 20 or 40 so it plays some weak card
        if (!card)
        {
          // suits of cards that the opponent has
          vector<Suits> hasSuits;
          for (int i=0; i<opponent->getCards().size(); i++)
          {
            Card *workCard = opponent->getCards().at(i);
            if (!vecContains(hasSuits, workCard->getSuit()))
            {
              hasSuits.push_back(workCard->getSuit());
            }
          }

          // chooses the weakest card that is not trump
          // or the card of suit that the opponent doesn't have
          Card *weakest = NULL;
          for (int i=0; i<availCards.size(); i++)
          {
            Card *workCard = availCards.at(i);
            if (workCard->getSuit() == game->getTrump())
            {
              continue;
            }
            if (!weakest)
            {
              weakest = workCard;
            }
            if (workCard->getCard() < weakest->getCard())
            {
              weakest = workCard;
            }
            if ((workCard->getCard() == weakest->getCard()) &&
                !vecContains(hasSuits, workCard->getSuit()))
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
      Card *oppCard = game->getOneCard()? game->getOneCard() : game->getTwoCard();

      if (game->getIsClosed())
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
        vector<Card *> sameSuitCards;
        for (int i=0; i<availCards.size(); i++)
        {
          Card *workCard = availCards.at(i);
          if (workCard->getSuit() == oppCard->getSuit())
          {
            sameSuitCards.push_back(workCard);
          }
        }
        sort(sameSuitCards.begin(), sameSuitCards.end(), cmpCard);

        // finds the trump cards we have
        vector<Card *> trumpCards;
        for (int i=0; i<availCards.size(); i++)
        {
          Card *workCard = availCards.at(i);
          if (workCard->getSuit() == game->getTrump())
          {
            trumpCards.push_back(workCard);
          }
        }
        sort(trumpCards.begin(), trumpCards.end(), cmpCard);

        // 2.
        if (!sameSuitCards.empty())
        {
          Card * strongest = sameSuitCards.back();
          Card * weakest = sameSuitCards.front();
          if (strongest->getCard() > oppCard->getCard())
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
          if (oppCard->getSuit() == game->getTrump())
          {
            // 3.a
            sort(availCards.begin(), availCards.end(), cmpNoSuit);
            card = availCards.front();
          }
          else
          {
            // 3.b
            if (!trumpCards.empty())
            {
              // the strongest trump? we have
              Card * strongest = trumpCards.back();
              card = strongest;
            }
            else
            {
              // or the weakest card we have
              sort(availCards.begin(), availCards.end(), cmpNoSuit);
              card = availCards.front();
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
        vector<Card *> pairCards;
        for (int i=0; i<availCards.size(); i++)
        {
          Card *workCard = availCards.at(i);
          if (workCard->getCard() == QUEEN)
          {
            for (int j=0; j<availCards.size(); j++)
            {
              Card *workCard2 = availCards.at(j);
              if ((workCard2->getCard() == KING) && 
                  (workCard2->getSuit() == workCard->getSuit()) )
              {
                pairCards.push_back(workCard);
                pairCards.push_back(workCard2);
                break;
              }
            }
          }
        }
        removeAll(availCards, pairCards);

        // finds all cards with the same suit as the opponent
        vector<Card *> sameSuitCards;
        for (int i=0; i<availCards.size(); i++)
        {
          Card *workCard = availCards.at(i);
          if (workCard->getSuit() == oppCard->getSuit())
          {
            sameSuitCards.push_back(workCard);
          }
        }
        sort(sameSuitCards.begin(), sameSuitCards.end(), cmpCard);

        // check if strongest card from the same suit is stronger than the opponent card
        if (!sameSuitCards.empty())
        {
          Card *strongest = sameSuitCards.back();
          if (strongest->getCard() > oppCard->getCard())
          {
            card = strongest;
          }
        }

        vector<Card *> trumpCards;
        for (int i=0; i<availCards.size(); i++)
        {
          Card *workCard = availCards.at(i);
          if (workCard->getSuit() == game->getTrump())
          {
            trumpCards.push_back(workCard);
          }
        }
        
        if (!card)
        {
          if (oppCard->getSuit() != game->getTrump())
          {
            if ((oppCard->getCard() == ACE) || (oppCard->getCard() == TEN))
            {
              // if opponent card is strong, try to take it with trump
              // take with the strongest trump card

              if (!trumpCards.empty())
              {
                sort(trumpCards.begin(), trumpCards.end(), cmpCard);
                card = trumpCards.back();
              }
            }
          }
        }

        if (!card)
        {
          // opponent card is not strong or we don't have trumps to play
          // return the weakest card we have(without trumps), no matter the suit
          removeAll(availCards, trumpCards);
          sort(availCards.begin(), availCards.end(), cmpNoSuit);
          card = availCards.front();
        }

      }
    }
  }

  // if not choosed - random card of availables
  if (!card)
  {
    int r = rand() % availCards.size();
    card = availCards.at(r);
  }

  if (!game->validateCard(this, card, true))
  {
    return;
  }

  game->getBoard()->fireCardMove(card);

  isChoosing = false;

  ss.str("");
  ss << *this << " chose card " << *card;
  game->getNotification()->info(ss.str());
  game->getNotification()->warning(ss.str());
  
  game->nextMove(card);
}

