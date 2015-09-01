#include "gui_board.h"
#include "abstract_player.h"
#include "gui_player.h"
#include "computer_player.h"
#include "sleeper.h"

GuiBoard::GuiBoard(QWidget *parent, Game *game) : QWidget(parent), IBoard()
{
//  cout << "Constructing GuiBoard" << endl;
  this->game = game;
  setMouseTracking(true);

  mx = 0;
  my = 0;
  movingCard = NULL;
  isTaking = false;
  tx1 = 0; ty1 = 0;
  tx2 = 0; ty2 = 0;  

  loadCardImages();

  connect(this, SIGNAL(repaintSignal()), this, SLOT(repaint()) );
}

GuiBoard::~GuiBoard()
{
//  cout << "Destroying GuiBoard" << endl;
  freeCardImages();
  freePoints();
}

void GuiBoard::paintEvent(QPaintEvent *event)
{
  QPainter painter(this);

  drawLogic(painter);
}

void GuiBoard::drawLogic(QPainter & painter)
{
  if (!game)
  {
    return;
  }
  QPen penBlack(Qt::black, 2, Qt::SolidLine);
  painter.setPen(penBlack);

  int width = painter.device()->width();
  int height = painter.device()->height();
  scaleCardImages(width);
  initPoints(width);
  painter.fillRect(0, 0, width, height, Qt::green);

  // deck and trump
  if (game->deckHasCards())
  {
    painter.drawImage(*pointDeck, *backImage);
    if (game->getIsClosed())
    {
      painter.drawImage(*pointDeck, *backHorizImage);
    }
    else
    {
      QImage *trumpImage = backImage;
      if (game->getTrumpCard())
      {
        trumpImage = getCardImage(game->getTrumpCard());
      }
      painter.drawImage(*pointTrump, *trumpImage);
    }
  }

  // playing cards
  painter.drawRect(pointOneCard->x() - 2, pointOneCard->y() - 2, backImage->width() + 4, backImage->height() + 4);
  if (game->getOneCard())
  {
    QImage *img = getCardImage(game->getOneCard());
    if (isTaking)
    {
      painter.drawImage(QPoint(tx1, ty1), *img);
    }
    else
    {
      painter.drawImage(*pointOneCard, *img);
    }
  }
  painter.drawRect(pointTwoCard->x() - 2, pointTwoCard->y() - 2, backImage->width() + 4, backImage->height() + 4);
  if (game->getTwoCard())
  {
    QImage *img = getCardImage(game->getTwoCard());
    if (isTaking)
    {
      painter.drawImage(QPoint(tx2, ty2), *img);
    }
    else
    {
      painter.drawImage(*pointTwoCard, *img);
    }
  }

  // first player
  vector<Card *> cardsPlayer1 = game->getPlayer1()->getCards();
  for (int i=0; i<min(cardsPlayer1.size(), pointsPlayer1.size()); i++)
  {
    Card * card = cardsPlayer1.at(i);
    QImage *img = getCardImage(card);
    if (movingCard && (card == movingCard))
    {
      painter.drawImage(QPoint(mx, my), *img);
    }
    else
    {
      if (!game->getIsOne() || dynamic_cast<ComputerPlayer *>(game->getPlayer1()) )
      {
        img = backImage;
      }
      QPoint *p = pointsPlayer1.at(i);
      painter.drawImage(*p, *img);
    }
  }
  // second player
  vector<Card *> cardsPlayer2 = game->getPlayer2()->getCards();
  for (int i=0; i<min(cardsPlayer2.size(), pointsPlayer2.size()); i++)
  {
    Card * card = cardsPlayer2.at(i);
    QImage *img = getCardImage(card);
    if (movingCard && (card == movingCard))
    {
      painter.drawImage(QPoint(mx, my), *img);
    }
    else
    {
      if (game->getIsOne() || dynamic_cast<ComputerPlayer *>(game->getPlayer2()) )
      {
        img = backImage;
      }
      QPoint *p = pointsPlayer2.at(i);
      painter.drawImage(*p, *img);
    }
  }
}

void GuiBoard::initPoints(int width)
{
  freePoints();
  pointsPlayer1.clear();
  pointsPlayer2.clear();

  int cwidth = width / 8;
  for (int i=0; i<CARDS_IN_HAND; i++)
  {
//    cout << "Constructing pointsPlayer1" << endl;
    float cx = (float) i * 1.4 * (float) cwidth;
    QPoint *p = new QPoint((int) cx, 5);
    pointsPlayer1.push_back(p);
  }
  for (int i=0; i<CARDS_IN_HAND; i++)
  {
//    cout << "Constructing pointsPlayer2" << endl;
    float cx = (float) i * 1.4 * (float) cwidth;
    QPoint *p = new QPoint((int) cx, 20 + 2 * backImage->height());
    pointsPlayer2.push_back(p);
  }
//  cout << "Constructing pointOneCard" << endl;
  float cx = 2.8 * (float) cwidth;
  int cy = 10 + backImage->height();
  pointOneCard = new QPoint((int) cx, cy);
//  cout << "Constructing pointTwoCard" << endl;
  cx = 4.2 * (float) cwidth;
  pointTwoCard = new QPoint((int) cx, cy);
//  cout << "Constructing pointDeck" << endl;
  pointDeck = new QPoint(0, cy);
//  cout << "Constructing pointTrump" << endl;
  pointTrump = new QPoint(cwidth + 2, cy);
}

void GuiBoard::freePoints()
{
  for (int i=0; i<pointsPlayer1.size(); i++)
  {
//    cout << "Destroying pointsPlayer1" << endl;
    QPoint *p = pointsPlayer1.at(i);
    delete p;
  }
  for (int i=0; i<pointsPlayer2.size(); i++)
  {
//    cout << "Destroying pointsPlayer2" << endl;
    QPoint *p = pointsPlayer2.at(i);
    delete p;
  }
//  cout << "Destroying pointOneCard" << endl;
  delete pointOneCard;
//  cout << "Destroying pointTwoCard" << endl;
  delete pointTwoCard;
//  cout << "Destroying pointDeck" << endl;
  delete pointDeck;
//  cout << "Destroying pointTrump" << endl;
  delete pointTrump;
}

void GuiBoard::loadCardImages()
{
  for (int i=0; i<24; i++)
  {
    string imageName = num2imageName(i);
//    cout << "Loading original image: "<< imageName << endl;
    QImage *img = new QImage(imageName.c_str());
    origCardImages.push_back(img);
  }
//  cout << "Loading image: origBackImage" << endl;
  origBackImage = new QImage(":/images/back.png");
//  cout << "Loading image: origBackHorizImage" << endl;
  origBackHorizImage = new QImage(":/images/back_horiz.png");
}

void GuiBoard::freeCardImages()
{
  for (int i=0; i<origCardImages.size(); i++)
  {
    QImage *img = origCardImages.at(i);
//    cout << "Destroying original image: cardImage" << endl;
    delete img;
  }
//  cout << "Destroying image: origBackImage" << endl;
  delete origBackImage;
//  cout << "Destroying image: origBackHorizImage" << endl;
  delete origBackHorizImage;

  for (int i=0; i<cardImages.size(); i++)
  {
    QImage *img = cardImages.at(i);
//    cout << "Destroying image: cardImage" << endl;
    delete img;
  }
//  cout << "Destroying image: backImage" << endl;
  delete backImage;
//  cout << "Destroying image: backHorizImage" << endl;
  delete backHorizImage;
}

QImage * GuiBoard::scaleCard(QImage *img, int width)
{
  float factor = (float) width / (float) img->width();
  QImage scaled = img->scaled(width, round(factor * (float) img->height()), Qt::KeepAspectRatio);
  QImage *result = new QImage(scaled);
  return result;
}

void GuiBoard::scaleCardImages(int width)
{
  for (int i=0; i<cardImages.size(); i++)
  {
    QImage *img = cardImages.at(i);
//    cout << "Destroying image: cardImage" << endl;
    delete img;
  }
//  cout << "Destroying image: backImage" << endl;
  if (backImage)
    delete backImage;
//  cout << "Destroying image: backHorizImage" << endl;
  if (backHorizImage)
    delete backHorizImage;

  cardImages.clear();

  int cwidth = width / 8;
  for (int i=0; i<origCardImages.size(); i++)
  {
    QImage *img = origCardImages.at(i);
    QImage *scaled = scaleCard(img, cwidth);
    cardImages.push_back(scaled);
  }
  backImage = scaleCard(origBackImage, cwidth);
  backHorizImage = scaleCard(origBackHorizImage, (int) (1.4 * (float)cwidth) );
}

string GuiBoard::num2imageName(int num)
{
  string cardNames[] = { "nine", "jack", "queen", "king", "ten", "ace" };
  string suitNames[] = { "clubs", "diamonds", "hearts", "spades" };
  int countSuits = 4;
  int c = num / countSuits;
  int s = num % countSuits;
  stringstream ss;
  ss << ":/images/" << cardNames[c] << "_" << suitNames[s] << ".png";
  return ss.str();
}

int GuiBoard::card2num(Card *card)
{
  if (card == NULL)
  {
    return -1;
  }

  Suits allSuits[] = { CLUB, DIAMOND, HEART, SPADE };
  Cards allCards[] = { NINE, JACK, QUEEN, KING, TEN, ACE };
  int countSuits = sizeof(allSuits) / sizeof(Suits);
  int countCards = sizeof(allCards) / sizeof(Cards);

  Suits s = card->getSuit();
  Cards c = card->getCard();
  
  int sn;
  for (sn = 0; sn<countSuits; sn++)
  {
    if (allSuits[sn] == s)
    {
      break;
    }
  }
  int cn;
  for (cn = 0; cn<countCards; cn++)
  {
    if (allCards[cn] == c)
    {
      break;
    }
  }
  return cn * countSuits + sn;
}

QImage * GuiBoard::getCardImage(Card *card)
{
  if (!card)
  {
    return NULL;
  }
  int num = card2num(card);
  return cardImages.at(num);
}

vector<QPoint *> GuiBoard::linePath(int x1, int y1, int x2, int y2, int steps)
{
  vector<QPoint *> result;
  if (steps == 0)
  {
    return result;
  }
  result.push_back(new QPoint(x1, y1));
  steps--;
  for (int i=1; i<=steps; i++)
  {
    QPoint *p = new QPoint((int) (x1 + i * (x2 - x1) / (double)steps), (int)(y1 + i * (y2 - y1) / (double)steps));
    result.push_back(p);
  }
  return result;
}

void GuiBoard::freeLinePath(vector<QPoint *> & path)
{
  for (int i=0; i<path.size(); i++)
  {
    QPoint *p = path.at(i);
    delete p;
  }
}

void GuiBoard::print()
{
  emit repaintSignal();
}

void GuiBoard::move(int x1, int y1, int x2, int y2)
{
  vector<QPoint *> path = linePath(x1, y1, x2, y2, 10);
  for (int i=0; i<path.size(); i++)
  {
    QPoint *p = path.at(i);
    mx = p->x();
    my = p->y();
    print();
    sleep(50);
  }
  freeLinePath(path);
}

void GuiBoard::fireCardMove(Card *card)
{
  vector<QPoint *> & points = game->getIsOne()? pointsPlayer1 : pointsPlayer2;
  QPoint * dest = game->getIsOne()? pointOneCard : pointTwoCard;
  
  vector<Card *> & cards = game->getIsOne()? game->getPlayer1()->getCards() :
                                             game->getPlayer2()->getCards();
  int index = -1;
  for (int i=0; i<cards.size(); i++)
  {
    Card *workCard = cards.at(i);
    if (card == workCard)
    {
      index = i;
      break;
    }
  }
  if (index >= 0)
  {
    movingCard = card;
    move(points[index]->x(), points[index]->y(), dest->x(), dest->y());
  }
}

void GuiBoard::fireTakeCards(bool isOne)
{
  int imgHeight = backImage->height();
  vector<QPoint *> pone = linePath(pointOneCard->x(), pointOneCard->y(), pointOneCard->x(), isOne? -imgHeight : height(), 4);
  vector<QPoint *> ptwo = linePath(pointTwoCard->x(), pointTwoCard->y(), pointTwoCard->x(), isOne? -imgHeight : height(), 4);

  isTaking = true;
  for (int i=0; i<min(pone.size(), ptwo.size()); i++)
  {
    QPoint *p1 = pone.at(i);
    QPoint *p2 = ptwo.at(i);
    tx1 = p1->x();
    ty1 = p1->y();
    tx2 = p2->x();
    ty2 = p2->y();
    print();
    sleep(50);
  }
  isTaking = false;
  freeLinePath(pone);
  freeLinePath(ptwo);
  fireResetMoves();
}

void GuiBoard::fireResetMoves()
{
  movingCard = NULL;
  mx = 0;
  my = 0;
  print();
}

void GuiBoard::mousePressEvent(QMouseEvent *event)
{
  if (event->button() == Qt::LeftButton) 
  {
    QPoint pos = event->pos();
    int x = pos.x();
    int y = pos.y();

    AbstractPlayer *absPlayer1 = game->getPlayer1();
    AbstractPlayer *absPlayer2 = game->getPlayer2();

    if ( (game->getIsOne() && !absPlayer1->getIsChoosing()) ||
         (!game->getIsOne() && !absPlayer2->getIsChoosing()) )
    {
      return;
    }

    int imgWidth = backImage->width();
    int imgHeight = backImage->height();

    // change trump card
    if (!game->getIsClosed() && game->getTrumpCard())
    {
      if ((x >= pointTrump->x()) && (x <= (pointTrump->x() + imgWidth)) &&
          (y >= pointTrump->y()) && (y <= (pointTrump->y() + imgHeight)) )
      {
        int result = QMessageBox::question(this, "Change trump card:", 
	  "Are you sure you want to change the trump card ?", 
          QMessageBox::Yes | QMessageBox::No );        
        if (result == QMessageBox::Yes)
        {
          AbstractPlayer *changePlayer = game->getIsOne()? absPlayer1 : absPlayer2;
          game->changeTrump(changePlayer);

          stringstream ss;
          ss.str("");
          ss << *changePlayer << " changed the trump card.";
          game->getNotification()->info(ss.str());
          return;
        }
      }
    }

    // close deck
    if (!game->getIsClosed())
    {
      if ((x >= pointDeck->x()) && (x <= (pointDeck->x() + imgWidth)) &&
          (y >= pointDeck->y()) && (y <= (pointDeck->y() + imgHeight)) )
      {
        int result = QMessageBox::question(this, "Close game:", 
	  "Are you sure you want to close the game ?", 
          QMessageBox::Yes | QMessageBox::No );        
        if (result == QMessageBox::Yes)
        {
          game->closeGame(game->getIsOne()? absPlayer1 : absPlayer2);
          return;
        }
      }
    }

    vector<Card *> cards;
    vector<QPoint *> points;

    if (game->getIsOne())
    {
      cards = absPlayer1->getCards();
      points = pointsPlayer1;
    }
    else
    {
      cards = absPlayer2->getCards();
      points = pointsPlayer2;
    }

    int index = -1;
    for (int i=0; i<points.size(); i++)
    {
      QPoint *p = points.at(i);
      if ((x >= p->x()) && (x <= (p->x() + imgWidth)) &&
          (y >= p->y()) && (y <= (p->y() + imgHeight)) )
      {
        index = i;
        break;
      }
    }

    if ((index >= 0) && (index < cards.size()))
    {
      Card *chosenCard = cards.at(index);
     
      if (game->getIsOne())
      {
        GuiPlayer *guiPlayer1 = dynamic_cast<GuiPlayer *>(absPlayer1);
        if (guiPlayer1)
        {
          guiPlayer1->setCard(chosenCard);
        }
      }
      else
      {
        GuiPlayer *guiPlayer2 = dynamic_cast<GuiPlayer *>(absPlayer2);
        if (guiPlayer2)
        {
          guiPlayer2->setCard(chosenCard);
        }
      }

    }
  }
}

void GuiBoard::mouseMoveEvent(QMouseEvent *event)
{
  QPoint pos = event->pos();
  int x = pos.x();
  int y = pos.y();

  AbstractPlayer *absPlayer1 = game->getPlayer1();
  AbstractPlayer *absPlayer2 = game->getPlayer2();

  if ( !absPlayer1->getIsChoosing() && !absPlayer2->getIsChoosing() )
  {
    return;
  }

  int imgWidth = backImage->width();
  int imgHeight = backImage->height();

  // trump card
  if ((x >= pointTrump->x()) && (x <= (pointTrump->x() + imgWidth)) &&
      (y >= pointTrump->y()) && (y <= (pointTrump->y() + imgHeight)) )
  {
    setToolTip(QString::fromStdString("Change trump card"));
    return;
  }

  // close deck
  if ((x >= pointDeck->x()) && (x <= (pointDeck->x() + imgWidth)) &&
      (y >= pointDeck->y()) && (y <= (pointDeck->y() + imgHeight)) )
  {
    setToolTip(QString::fromStdString("Close the game"));
    return;
  }

  // player 1
  /*for (int i=0; i<min(absPlayer1->getCards().size(), pointsPlayer1.size()); i++)
  {
    QPoint *p = pointsPlayer1.at(i);
    if ((x >= p->x()) && (x <= (p->x() + imgWidth)) &&
        (y >= p->y()) && (y <= (p->y() + imgHeight)) )
    {
      setToolTip(QString::fromStdString("Choose card"));
      return;
    }
  }

  // player 2
  for (int i=0; i<min(absPlayer2->getCards().size(), pointsPlayer2.size()); i++)
  {
    QPoint *p = pointsPlayer2.at(i);
    if ((x >= p->x()) && (x <= (p->x() + imgWidth)) &&
        (y >= p->y()) && (y <= (p->y() + imgHeight)) )
    {
      setToolTip(QString::fromStdString("Choose card"));
      return;
    }
  }*/
  
  setToolTip(QString());
}


