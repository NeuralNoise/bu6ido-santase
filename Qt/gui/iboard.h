#ifndef _IBOARD_H
#define _IBOARD_H

#include "common.h"
#include "card.h"

class IBoard
{
  public:
    IBoard();
    virtual ~IBoard();
    virtual void print() = 0;
    virtual void fireCardMove(Card *card) = 0;
    virtual void fireTakeCards(bool isOne) = 0;
    virtual void fireResetMoves() = 0;
};

#endif
