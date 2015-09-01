#include "console_board.h"
#include "abstract_player.h"
#include "computer_player.h"

ConsoleBoard::ConsoleBoard(Game *game)
{
//  cout << "Constructing ConsoleBoard" << endl;
  this->game = game;
}

ConsoleBoard::~ConsoleBoard()
{
//  cout << "Destroying ConsoleBoard" << endl;
}

void ConsoleBoard::print()
{
  vector<Card *> & cards = game->getPlayer1()->getCards();
  AbstractPlayer *player = game->getPlayer1();
  ComputerPlayer *compPlayer = dynamic_cast<ComputerPlayer *>(player);

  cout << *player << "'s cards are: ";
  for (int i=0; i<cards.size(); i++)
  {
    Card *card = cards.at(i);
    if (compPlayer)
    {
      cout << "XX ";
    }
    else
    {
      cout << *card << " ";
    }
  }
  cout << endl;
}

void ConsoleBoard::fireCardMove(Card *card)
{
}

void ConsoleBoard::fireTakeCards(bool isOne)
{
}

void ConsoleBoard::fireResetMoves()
{
}

