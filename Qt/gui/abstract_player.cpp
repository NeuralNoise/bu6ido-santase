#include "abstract_player.h"
#include "game.h"

AbstractPlayer::AbstractPlayer(Game *game, string name)
{
//  cout << "Creating AbstractPlayer" << endl;
  this->game = game;
  this->name = name;
  isChoosing = false;
  clearCards();
}

AbstractPlayer::~AbstractPlayer()
{
  clearCards();
//  cout << "Destroying AbstractPlayer" << endl;
}

void AbstractPlayer::clearCards()
{
  cards.clear();
  takenCards.clear();
  pairs.clear();
}

string AbstractPlayer::getName()
{
  return name;
}

void AbstractPlayer::setName(string name)
{
  this->name = name;
}

vector<Card *> & AbstractPlayer::getCards()
{
  return cards;
}

vector<Card *> & AbstractPlayer::getTakenCards()
{
  return takenCards;
}

void AbstractPlayer::setTakenCards(vector<Card *> & takenCards)
{
  this->takenCards = takenCards;
}

void AbstractPlayer::addCard(Card *card)
{
  cards.push_back(card);
}

void AbstractPlayer::removeCard(Card *card)
{
  removeItem(cards, card);
}

void AbstractPlayer::addTakenCard(Card *card)
{
  takenCards.push_back(card);
}

void AbstractPlayer::addPair(Suits pairSuit)
{
  pairs.push_back(pairSuit);
}

int AbstractPlayer::getScore()
{
  int score = 0;
  for (int i=0; i<takenCards.size(); i++)
  {
    Card *card = takenCards.at(i);
    if (card)
    {
      score += card->getCard();
    }
  }
  Suits trump = game->getTrump();
  for (int i=0; i<pairs.size(); i++)
  {
    Suits suit = pairs.at(i);
    if (suit == trump)
    {
      score += SCORE_TRUMP_PAIR;
    }
    else
    {
      score += SCORE_PAIR;
    }
  }
  return score;
}

bool AbstractPlayer::getIsChoosing()
{
  return isChoosing;
}

void AbstractPlayer::play()
{
  // using current cards
  // call game.nextMove(PlayCard)
  sort(cards.begin(), cards.end(), cmpCard);
  stringstream ss;
  ss.str("");
  ss << *this << " is in turn to play.";
  if (game)
  {
    game->getNotification()->info(ss.str());
  }
  if (game && game->getBoard())
  {
    game->getBoard()->print();
  }
}

ostream & operator<<(ostream & os, AbstractPlayer & player)
{
  os << player.getName();
  return os;
}
