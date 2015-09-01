#ifndef _COMMON_H
#define _COMMON_H

#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
#include <sstream>

#include <cstdlib>
#include <ctime>

using namespace std;

#define CARDS_IN_HAND 6

#define SCORE_TRUMP_PAIR 40
#define SCORE_PAIR 20
#define HALF_SCORE 33
#define FINAL_SCORE 66

enum Suits { CLUB, DIAMOND, HEART, SPADE }; // spatia, karo, kupa, pika
enum Cards { NINE = 0, JACK = 2, QUEEN = 3, KING = 4, TEN = 10, ACE = 11 }; // devet, vale, dama, pop, deset, aso

// utility
template <typename T> void removeItem(vector<T *> &vect, T *obj)
{
  typename vector<T *>::iterator newEnd = remove(vect.begin(), vect.end(), obj);
  vect.erase(newEnd, vect.end());
}

template <typename T> void removePos(vector<T> & vect, int pos)
{
  vect.erase(vect.begin() + pos);
}

template <typename T> void removeAll(vector<T> & v1, vector<T> & v2)
{
  vector<T> diff;
  set_difference(v1.begin(), v1.end(), v2.begin(), v2.end(), inserter(diff, diff.begin()) );
  v1 = diff;
}

template <typename T> bool vecContains(vector<T> & vect, T obj)
{
  typename vector<T>::iterator iter = find(vect.begin(), vect.end(), obj);
  return (iter != vect.end());
}

#endif

