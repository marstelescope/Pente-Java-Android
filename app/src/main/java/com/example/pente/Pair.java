/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;

/**
 Class to simulate a key-value pair
 */
// https://www.educative.io/answers/how-to-create-a-pair-class-in-java
class Pair<T> 
{
  private T p1, p2;
  Pair() {}

  /**
   Gets first value of a pair
   @return first value of pair
   */
  T getFirst(){
    return this.p1;
  }

  /**
   Gets second value of a pair
   @return second value of pair
   */
  T getSecond(){
    return this.p2;
  }

  /**
   Sets value of a pair.
   @param a - Template type, first value of pair
   @param b - Template type, second of pair
   */
  void setValue(T a, T b)
  {
    this.p1 = a;
    this.p2 = b;
  }
  
}