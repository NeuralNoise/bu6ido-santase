#ifndef _CONSOLE_BOARD_H
#define _CONSOLE_BOARD_H

#include "iboard.h"
#include "card.h"
#include "game.h"

class ConsoleBoard : public IBoard
{
  public:
    ConsoleBoard(Game *game);
    virtual ~ConsoleBoard();
    void print();
    void fireCardMove(Card *card);
    void fireTakeCards(bool isOne);
    void fireResetMoves();

  private:
    Game *game;
};

#endif
