#ifndef _CONSOLE_NOTIFICATION_H
#define _CONSOLE_NOTIFICATION_H

#include "inotification.h"

class ConsoleNotification : public INotification
{
  public:
    ConsoleNotification();
    virtual ~ConsoleNotification();
    void info(string message);
    void warning(string message);
    void status(string message);
};

#endif
