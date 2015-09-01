#include "console_notification.h"

ConsoleNotification::ConsoleNotification()
{
//  cout << "Constructing ConsoleNotification" << endl;
}

ConsoleNotification::~ConsoleNotification()
{
//  cout << "Destroying ConsoleNotifcation" << endl;
}

void ConsoleNotification::info(string message)
{
  cout << message << endl;
}

void ConsoleNotification::warning(string message)
{
  cout << "WARNING: " << message << endl;
}

void ConsoleNotification::status(string message)
{
  cout << "STATUS: " << message << endl;
}

