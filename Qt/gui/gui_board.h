#ifndef _GUI_BOARD_H
#define _GUI_BOARD_H

#include "common.h"
#include "card.h"
#include "iboard.h"
#include "game.h"

#include <QtGui>

class GuiBoard : public QWidget, public IBoard
{
  Q_OBJECT

  public:
    GuiBoard(QWidget *parent = NULL, Game *game = NULL);
    virtual ~GuiBoard();
    void print();
    void fireCardMove(Card *card); 
    void fireTakeCards(bool isOne);
    void fireResetMoves();
    void move(int x1, int y1, int x2, int y2);

  signals:
    void repaintSignal();

  protected:
    void paintEvent(QPaintEvent *event);
    void drawLogic(QPainter & painter);
    void mousePressEvent(QMouseEvent *event);
    void mouseMoveEvent(QMouseEvent *event);

    void initPoints(int width);
    void freePoints();

    void loadCardImages();
    void freeCardImages();
    string num2imageName(int num);
    int card2num(Card *card);
    QImage *getCardImage(Card *card);

    QImage * scaleCard(QImage *img, int width);
    void scaleCardImages(int width);

    vector<QPoint *> linePath(int x1, int y1, int x2, int y2, int steps);
    void freeLinePath(vector<QPoint *> & path);

  private:
    Game *game;

    vector<QImage *> origCardImages;
    QImage * origBackImage;
    QImage * origBackHorizImage;

    vector<QImage *> cardImages;
    QImage *backImage;
    QImage *backHorizImage;

    vector<QPoint *> pointsPlayer1;
    vector<QPoint *> pointsPlayer2;
    QPoint *pointOneCard;
    QPoint *pointTwoCard;
    QPoint *pointDeck;
    QPoint *pointTrump;

    int mx, my;
    Card *movingCard;
   
    bool isTaking;
    int tx1, ty1, tx2, ty2;

};

#endif

