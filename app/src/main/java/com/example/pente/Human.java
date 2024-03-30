/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;

import java.io.Console;

public class Human extends Player {
    Human(Board a_board){
        super(a_board);
    }

    /**
     Places move for the human
     @param move - String, move the human tries to place
     @return empty string if move placed successfully, error encountered otherwise
     */
    public String makeTurn(String move){
        String ret = m_board.makeMove(m_board.getHumanColor(), move);
        return ret;
    }
}
