#ifndef _SANTASE_MAIN_WINDOW
#define _SANTASE_MAIN_WINDOW

#include "common.h"
#include "iboard.h"
#include "abstract_player.h"
#include "game.h"

#include <QtGui>

#include "gui_board.h"
#include "gui_notification.h"
#include "settings_dialog.h"

class GameThread : public QThread
{
  public:
    GameThread(QObject *parent, Game *game);
    virtual ~GameThread();
    virtual void run();

  private:
    Game * game;
};

class SantaseMainWindow : public QMainWindow
{
  Q_OBJECT

  public:
    SantaseMainWindow(QWidget *parent = NULL);
    virtual ~SantaseMainWindow();

    void createActions();
    void createMenuBar();

  private:
    void closeEvent(QCloseEvent *event);

    QSplitter *splitter;
    QList<int> splitSizes;
    GuiBoard *board;
    
    Game *game;
    GuiNotification *notification;
    AbstractPlayer *player1;
    AbstractPlayer *player2;
    
    QAction *actNewGame;
    QAction *actSettings;
    QAction *actAboutQt;
    QAction *actAbout;
    QMenu *menuSantase;
    QMenu *menuHelp;

    GameThread *gameThread;

    SettingsDialog * dlgSettings;

  private slots:
    void on_new_game();  
    void on_settings();
    void on_about();
};

#endif
