#include "console_player.h"

ConsolePlayer::ConsolePlayer(Game *game, string name) : AbstractPlayer(game, name)
{
//  cout << "Constructing ConsolePlayer" << endl;
}

ConsolePlayer::~ConsolePlayer()
{
//  cout << "Destroying ConsolePlayer" << endl;
}

void ConsolePlayer::play()
{
  AbstractPlayer::play();

  Card *card = NULL;
  int choice;
  string str;

  isChoosing = true;
  do
  {
    cout << "Please choose card (1.." << cards.size() << ", (C)lose, Change (T)rump): ";
    cin >> str;
    if (str.compare("c") == 0)
    {
      game->closeGame(this);
      continue;
    }
    if (str.compare("t") == 0)
    {
      game->changeTrump(this);
      continue;
    }
    choice = atoi(str.c_str());
    if ((choice >= 1) && (choice <= cards.size()))
    {
      card = cards.at(choice-1);
    }
    else
    {
      card = NULL;
    }
  }
  while (!game->validateCard(this, card, true));

  game->getBoard()->fireCardMove(card);

  isChoosing = false;

  stringstream ss;
  ss.str("");
  ss << *this << " chose card " << *card;
  game->getNotification()->info(ss.str());

  game->nextMove(card);
}

