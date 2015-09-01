#include "game.h"
#include "console_board.h"
#include "console_notification.h"
#include "console_player.h"
#include "computer_player.h"
#include "game.h"

int main(int argc, char *argv[])
{
  Game game;
  ConsoleBoard board(&game);
  ConsoleNotification notification;
  ConsolePlayer player1(&game, "First");
  ComputerPlayer player2(&game, "Second");
  game.setBoard(&board);
  game.setNotification(&notification);
  game.setPlayer1(&player1);
  game.setPlayer2(&player2);
  game.startNewGame();

  return 0;
}


