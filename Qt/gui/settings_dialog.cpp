#include "settings_dialog.h"

SettingsDialog::SettingsDialog(QWidget *parent) : QDialog(parent)
{
//  cout << "Constructing SettingsDialog" << endl;

  setWindowTitle("Game settings:");
  setModal(true);

  QGridLayout *grid = new QGridLayout(this);
  grid->setSpacing(5);

  lblNamePlayer1 = new QLabel("Name of Player 1:", this);
  grid->addWidget(lblNamePlayer1, 0, 0, 1, 1);

  leNamePlayer1 = new QLineEdit(this);
  grid->addWidget(leNamePlayer1, 0, 1, 1, 1);

  lblNamePlayer2 = new QLabel("Name of Player 2:", this);
  grid->addWidget(lblNamePlayer2, 1, 0, 1, 1);

  leNamePlayer2 = new QLineEdit(this);
  grid->addWidget(leNamePlayer2, 1, 1, 1, 1);

  chkRaising = new QCheckBox("Raising required", this);
  grid->addWidget(chkRaising, 2, 0, 1, 2);

  btnSave = new QPushButton("Save", this);
  grid->addWidget(btnSave, 3, 0, 1, 2);

  setLayout(grid);

  initLogic();
}

SettingsDialog::~SettingsDialog()
{
//  cout << "Destroying SettingsDialog" << endl;
}

void SettingsDialog::initLogic()
{
  connect(btnSave, SIGNAL(clicked()), this, SLOT(on_btnsave_clicked()) );

  loadSettings();
}

void SettingsDialog::loadSettings()
{
  QFile file("qsantase.dat");
  bool ok = file.open(QIODevice::ReadOnly);
  if (ok)
  {
    QDataStream stream(&file);
    stream >> settings;
  }

  leNamePlayer1->setText(QString::fromStdString(settings.getNamePlayer1()));
  leNamePlayer2->setText(QString::fromStdString(settings.getNamePlayer2()));
  chkRaising->setCheckState(settings.getIsRaising()? Qt::Checked : Qt::Unchecked);
}

void SettingsDialog::saveSettings()
{
  settings.setNamePlayer1(leNamePlayer1->text().toStdString());
  settings.setNamePlayer2(leNamePlayer2->text().toStdString());
  settings.setIsRaising(chkRaising->checkState() == Qt::Checked);

  QFile file("qsantase.dat");
  bool ok = file.open(QIODevice::WriteOnly);
  if (ok)
  {
    QDataStream stream(&file);
    stream << settings;
  }
}

void SettingsDialog::on_btnsave_clicked()
{
  saveSettings();
  accept();
}

