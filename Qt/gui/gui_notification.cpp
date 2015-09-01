#include "gui_notification.h"
#include "sleeper.h"

#include <QtGui>

GuiNotification::GuiNotification(QMainWindow *parent) : QTextEdit(parent)
{
//  cout << "Constructing GuiNotification" << endl;
  this->parent = parent;
  setReadOnly(true);

  connect(this, SIGNAL(infoSignal(QString)), this, SLOT(on_info(QString)) );
  connect(this, SIGNAL(warningSignal(QString)), this, SLOT(on_warning(QString)) );
  connect(this, SIGNAL(statusSignal(QString)), this, SLOT(on_status(QString)) );
}

GuiNotification::~GuiNotification()
{
//  cout << "Destroying GuiNotification" << endl;
}

void GuiNotification::info(string message)
{
  emit infoSignal(QString::fromStdString(message));
}

void GuiNotification::warning(string message)
{
  wait = true;
  emit warningSignal(QString::fromStdString(message));
  while (wait) sleep(10);
}

void GuiNotification::status(string message)
{
  emit statusSignal(QString::fromStdString(message));
}

void GuiNotification::on_info(QString message)
{
  append(message);
}

void GuiNotification::on_warning(QString message)
{
  if (parent)
  {
    QMessageBox::information(parent, "Warning:", message, QMessageBox::Ok);
  }
  wait = false;
  sleep(10);
}

void GuiNotification::on_status(QString message)
{
  if (parent)
  {
    parent->statusBar()->showMessage(message);
  }
}

