#ifndef _ABSTRACT_PLAYER_H
#define _ABSTRACT_PLAYER_H

#include "common.h"
#include "card.h"

class Game;
class AbstractPlayer
{
  public:
    AbstractPlayer(Game *game, string name);
    virtual ~AbstractPlayer();
    void clearCards();
    string getName();
    void setName(string name);
    vector <Card *> & getCards();
    vector <Card *> & getTakenCards();
    void setTakenCards(vector<Card *> & takenCards);
    void addCard(Card *card);
    void removeCard(Card *card);
    void addTakenCard(Card *takenCard);
    void addPair(Suits pairSuit);
    int getScore();
    bool getIsChoosing();
    virtual void play() = 0;
  protected:
    Game *game;
    string name;
    vector<Card *> cards;
    vector<Card *> takenCards;
    vector<Suits> pairs;
    bool isChoosing;
};
ostream & operator<<(ostream & os, AbstractPlayer & player);

#endif
