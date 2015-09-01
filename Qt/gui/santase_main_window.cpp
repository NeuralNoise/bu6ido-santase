#include "santase_main_window.h"
#include "gui_notification.h"
#include "gui_player.h"
#include "computer_player.h"
#include "sleeper.h"

SantaseMainWindow::SantaseMainWindow(QWidget *parent) : QMainWindow(parent)
{
//  cout << "Constructing SantaseMainWindow" << endl;
  setWindowTitle("QSantase - version 1.0");

  game = new Game();
  board = new GuiBoard(this, game);
  notification = new GuiNotification(this);
  splitter = new QSplitter(this);
  splitter->setOrientation(Qt::Horizontal);
  splitter->addWidget(board);
  splitter->addWidget(notification);
  setCentralWidget(splitter);
  resize(800, 500);

  splitSizes.clear();
  splitSizes.push_back(600);
  splitSizes.push_back(200);
  splitter->setSizes(splitSizes);

  player1 = new GuiPlayer(game, "First");
  player2 = new ComputerPlayer(game, "Second");
  game->setBoard(board);
  game->setNotification(notification);
  game->setPlayer1(player1);
  game->setPlayer2(player2);

  gameThread = NULL;

  dlgSettings = new SettingsDialog(this);

  createActions();
  createMenuBar();
  statusBar();
}

SantaseMainWindow::~SantaseMainWindow()
{
//  cout << "Destroying SantaseMainWindow" << endl;
  if (gameThread)
  {
    game->setIsInterrupted(true);
    sleep(250);
    delete gameThread;
  }

  delete player2;
  delete player1;
  delete notification;
  delete game;
}

void SantaseMainWindow::closeEvent(QCloseEvent *event)
{
  int result = QMessageBox::question(this, "Quit QSantase:", 
	"Are you sure you want to quit QSantase ?", 
        QMessageBox::Yes | QMessageBox::No );
  if (result == QMessageBox::Yes)
  {
    event->accept();
  }
  else 
  {
    event->ignore();
  }
}

void SantaseMainWindow::createActions()
{
  actNewGame = new QAction("&New game", this);
  actNewGame->setStatusTip("Starts new Santase game");
  connect(actNewGame, SIGNAL(triggered()), this, SLOT(on_new_game()) );

  actSettings = new QAction("&Settings", this);
  actSettings->setStatusTip("Changes game settings");
  connect(actSettings, SIGNAL(triggered()), this, SLOT(on_settings()) );

  actAboutQt = new QAction("About &Qt", this);
  actAboutQt->setStatusTip("Information about Qt framework license");
  connect(actAboutQt, SIGNAL(triggered()), qApp, SLOT(aboutQt()) );

  actAbout = new QAction("&About...", this);
  actAbout->setStatusTip("About QSantase");
  connect(actAbout, SIGNAL(triggered()), this, SLOT(on_about()) );
}

void SantaseMainWindow::createMenuBar()
{
  menuSantase = menuBar()->addMenu("&Santase");
  menuSantase->addAction(actNewGame);
  menuSantase->addAction(actSettings);

  menuHelp = menuBar()->addMenu("&Help");
  menuHelp->addAction(actAboutQt);
  menuHelp->addAction(actAbout);
}

void SantaseMainWindow::on_new_game()
{
  if (notification)
  {
    notification->clear();
  }

  if (gameThread)
  {
    game->setIsInterrupted(true);
    sleep(250);
    delete gameThread;
  }

  gameThread = new GameThread(this, game);
  gameThread->start();
}

void SantaseMainWindow::on_settings()
{
  dlgSettings->loadSettings();
  dlgSettings->show();
}

void SantaseMainWindow::on_about()
{
    QMessageBox::about(this, "About QSantase:",
      "<h2>QSantase 1.0</h2>"
      "<p><i>An implementation of the popular card game <br>'Sixty-six'('Shnapsen')</i></p>"
      "<p>Copyright &copy; 2014 Ivailo Georgiev</p>");
}

// SantaseGameThread

GameThread::GameThread(QObject *parent, Game *game) : QThread(parent)
{
//  cout << "Constructing GameThread" << endl;
  this->game = game;
}

GameThread::~GameThread()
{
//  cout << "Destroying GameThread" << endl;
}

void GameThread::run()
{
  if (game)
  {
    game->startNewGame();
  }
}

