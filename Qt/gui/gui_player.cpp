#include "gui_player.h"
#include "sleeper.h"

GuiPlayer::GuiPlayer(Game *game, string name) : AbstractPlayer(game, name)
{
//  cout << "GuiPlayer created" << endl;
}

GuiPlayer::~GuiPlayer()
{
//  cout << "GuiPlayer destroyed" << endl;
}

void GuiPlayer::setCard(Card *card)
{
  this->card = card;
}

void GuiPlayer::play()
{
  AbstractPlayer::play();

  isChoosing = true;

  card = NULL;

  while (!card || !game->validateCard(this, card, true))
  {
    if (game->getIsInterrupted())
    {
      return;
    }
    if (card)
    {
      card = NULL;
    }
    sleep(10);
  }

  game->getBoard()->fireCardMove(card);

  isChoosing = false;

  stringstream ss;
  ss.str("");
  ss << *this << " chose card " << *card;
  game->getNotification()->info(ss.str());
  
  game->nextMove(card);
}

