#ifndef _CARD_H
#define _CARD_H

#include "common.h"

class Card 
{
  public:
    Card(Cards c, Suits s);
    virtual ~Card();
    Cards getCard();
    Suits getSuit();

  private:
    Cards c;
    Suits s;
};
ostream & operator<<(ostream & os, Card & card);

bool cmpCard(Card *a, Card *b);
bool cmpNoSuit(Card *a, Card *b);

#endif
