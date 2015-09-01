#ifndef _INOTIFICATION_H
#define _INOTIFICATION_H

#include "common.h"

class INotification
{
  public:
    INotification();
    virtual ~INotification();
    virtual void info(string message) = 0;
    virtual void warning(string message) = 0;
    virtual void status(string message) = 0;
};

#endif
