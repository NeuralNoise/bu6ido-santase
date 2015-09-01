#ifndef _SLEEPER_H
#define _SLEEPER_H

#include <QtGui>

class Sleeper : public QThread
{
  public:
    void sleep(int ms) { QThread::msleep(ms); };
};

void sleep(int ms);

#endif

