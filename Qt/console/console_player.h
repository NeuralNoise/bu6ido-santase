#ifndef _CONSOLE_PLAYER_H
#define _CONSOLE_PLAYER_H

#include "abstract_player.h"
#include "game.h"

class ConsolePlayer : public AbstractPlayer
{
  public:
    ConsolePlayer(Game *game, string name);
    ~ConsolePlayer();
    void play();
};

#endif

