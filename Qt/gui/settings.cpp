#include "settings.h"

Settings::Settings()
{
//  cout << "Constructing Settings" << endl;
  namePlayer1 = "First";
  namePlayer2 = "Second";
  isRaising = true;
}

Settings::~Settings()
{
//  cout << "Destroying Settings" << endl;
}

string Settings::getNamePlayer1()
{
  return namePlayer1;
}

void Settings::setNamePlayer1(string namePlayer1)
{
  this->namePlayer1 = namePlayer1;
}

string Settings::getNamePlayer2()
{
  return namePlayer2;
}

void Settings::setNamePlayer2(string namePlayer2)
{
  this->namePlayer2 = namePlayer2;
}

bool Settings::getIsRaising()
{
  return isRaising;
}

void Settings::setIsRaising(bool isRaising)
{
  this->isRaising = isRaising;
}

QDataStream & operator<<(QDataStream & stream, Settings & settings)
{
  stream << QString::fromStdString(settings.getNamePlayer1());
  stream << QString::fromStdString(settings.getNamePlayer2());
  stream << settings.getIsRaising();
  return stream;
}

QDataStream & operator>>(QDataStream & stream, Settings & settings)
{
  QString name1, name2;
  bool isRaising;
  stream >> name1;
  settings.setNamePlayer1(name1.toStdString());
  stream >> name2;
  settings.setNamePlayer2(name2.toStdString());
  stream >> isRaising;
  settings.setIsRaising(isRaising);
  return stream;
}

