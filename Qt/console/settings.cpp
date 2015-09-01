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


