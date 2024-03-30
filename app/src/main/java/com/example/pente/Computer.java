/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;
public class Computer extends Player{
    Computer(Board a_board){
        super(a_board);
    }

    /**
     Places move for the computer using the strategy
     @param m - String, not used; used in Human implementation
     @return strategy used and move as a string
     */
    public String makeTurn (String m){
        String move = suggestMove();
        String[] words = move.split("\\s+");
        m_board.makeMove(m_board.getCompColor(), words[words.length - 1]);
        return move;
    }

    public static void main(String[] args){

    }
}
