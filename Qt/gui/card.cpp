#include "card.h"

Card::Card(Cards c, Suits s)
{
//  cout << "Constructing Card" << endl;
  this->c = c;
  this->s = s;
}

Card::~Card()
{
//  cout << "Destroying Card" << endl;
}

Cards Card::getCard()
{
  return c;
}

Suits Card::getSuit()
{
  return s;
}

ostream & operator<<(ostream & os, Card & card)
{
  Cards c = card.getCard();
  Suits s = card.getSuit();

  switch (c)
  {
    case NINE:
      os << "9";
      break;
    case JACK:
      os << "J";
      break;
    case QUEEN:
      os << "Q";
      break;
    case KING:
      os << "K";
      break;
    case TEN:
      os << "10";
      break;
    case ACE:
      os << "A";
      break;
    default:
      os << "(unknown)";
  }

  switch (s)
  {
    case CLUB:
      os << "\u2663(sp)"; //"\u2667";
      break;
    case DIAMOND:
      os << "\u2666(ka)"; //"\u2662";
      break;
    case HEART:
      os << "\u2665(ku)"; //"\u2661";
      break;
    case SPADE:
      os << "\u2660(pi)"; //"\u2664";
      break;
    default:
      os << "(unknown)";
  }
  
  return os;
}

bool cmpCard(Card *a, Card *b)
{
  Cards ca = a->getCard();
  Suits sa = a->getSuit();
  Cards cb = b->getCard();
  Suits sb = b->getSuit();

  if (sa == sb)
  {
    return ca < cb;
  }
  else
  {
    return sa < sb;
  }
}

bool cmpNoSuit(Card *a, Card *b)
{
  Cards ca = a->getCard();
  Cards cb = b->getCard();
  return ca < cb;
}

