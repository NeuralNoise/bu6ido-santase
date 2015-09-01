#ifndef _GUI_NOTIFICATION_H
#define _GUI_NOTIFICATION_H

#include "common.h"
#include "inotification.h"

#include <QtGui>

class GuiNotification : public QTextEdit, public INotification
{
  Q_OBJECT

  public:
    GuiNotification(QMainWindow *parent = NULL);
    virtual ~GuiNotification();
    void info(string message);
    void warning(string message);
    void status(string message);

  signals:
    void infoSignal(QString message);
    void warningSignal(QString message);
    void statusSignal(QString message);

  private:
    QMainWindow *parent;
    volatile bool wait;

  private slots:
    void on_info(QString message);
    void on_warning(QString message);
    void on_status(QString message);
};

#endif

