#ifndef _COMPUTER_PLAYER_H
#define _COMPUTER_PLAYER_H

#include "common.h"
#include "abstract_player.h"
#include "game.h"

class ComputerPlayer : public AbstractPlayer
{
  public:
    ComputerPlayer(Game *game, string name);
    virtual ~ComputerPlayer();
    void play();

  private:
    Card *card;
    void checkForTrumpCardChange();
    void checkForGameClose();
};

#endif

