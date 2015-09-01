#ifndef _SETTINGS_DIALOG_H
#define _SETTINGS_DIALOG_H

#include "common.h"
#include "settings.h"

#include <QtGui>

class SettingsDialog : public QDialog
{
  Q_OBJECT

  public:
    SettingsDialog(QWidget *parent = 0);
    virtual ~SettingsDialog();
    void initLogic();
    void loadSettings();
    void saveSettings();

  private:
    QLabel *lblNamePlayer1, *lblNamePlayer2;
    QLineEdit *leNamePlayer1, *leNamePlayer2;
    QCheckBox * chkRaising;

    QPushButton *btnSave;

    Settings settings;

  private slots:
    void on_btnsave_clicked();
};

#endif

