#include "sleeper.h"

static Sleeper sleeper;

void sleep(int ms)
{
  sleeper.sleep(ms);
}
