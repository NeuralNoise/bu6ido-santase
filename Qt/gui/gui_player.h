#ifndef _GUI_PLAYER_H
#define _GUI_PLAYER_H

#include "common.h"
#include "card.h"
#include "abstract_player.h"
#include "game.h"

class GuiPlayer : public AbstractPlayer
{
  public:
    GuiPlayer(Game *game, string name);
    virtual ~GuiPlayer();
    void setCard(Card *card);
    void play();

  private:
    Card *card;
};

#endif

