#ifndef _GAME_H
#define _GAME_H

#include "common.h"
#include "card.h"
#include "iboard.h"
#include "inotification.h"
#include "abstract_player.h"
#include "settings.h"

class Game
{
  public:
    Game();
    virtual ~Game();
    bool getIsInterrupted();
    void setIsInterrupted(bool isInterrupted);
    IBoard * getBoard();
    void setBoard(IBoard *board);
    INotification * getNotification();
    void setNotification(INotification *notification);
    AbstractPlayer * getPlayer1();
    void setPlayer1(AbstractPlayer *player1);
    AbstractPlayer * getPlayer2();
    void setPlayer2(AbstractPlayer *player2);
    bool firstCardToDrop(); // dali e pod ruka(igrae purvi)
    bool firstCardDropped(); // dali predi6niq igra4 e bil pod ruka(teku6tiq igrae vtori)
    Card * getOneCard();
    Card * getTwoCard();
    Card * getTrumpCard();
    void setTrumpCard(Card *trumpCard);
    Suits getTrump();
    bool getIsOne();
    bool deckHasCards();
    bool deckNotEnough();
    Card * removeFromDeck();
    bool getIsClosed();
    void setIsClosed(bool isClosed);
    void closeGame(AbstractPlayer *player);
    void changeTrump(AbstractPlayer *player);
    void printStatus();
    bool validateCard(AbstractPlayer *player, Card *card, bool showWarnings);
    bool checkGameOver();
    void startNewGame();
    void nextMove(Card *card);
  private:
    bool isInterrupted;
    bool lastWon;

    IBoard * board;
    INotification * notification;
    vector<Card *> cards;
    void initCards();
    void freeCards();
    void initDeck();

    vector<Card *> deck;
    Card *trumpCard;
    bool isOne;
    AbstractPlayer *player1;
    AbstractPlayer *player2;
    Card *oneCard;
    Card *twoCard;
    bool isClosed;
    AbstractPlayer *closePlayer;
    bool firstCardBetter(Card *first, Card *second);
    
    Settings settings;
    void loadSettings();
};


#endif

