/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;

public class Round {
    private boolean m_exitCall;
    private Board m_board;
    private Player[] m_roster;

    public Round(Board a_board, Player[] a_roster) {
        m_exitCall = false;
        m_board = a_board;
        m_roster = a_roster;
    }

    public boolean exitCall() {
        return m_exitCall;
    }

    /**
     Simulates round, altering players to make a turn
     @param a_index - int, index of current player
     */
    public void play(int a_index) {
        while (m_board.gameOver().equals("") && !m_board.isStop()) {
            m_board.display();
            m_roster[a_index].makeTurn("");
            a_index ^= 1;
        }
    
        if (m_board.isStop()) {
            m_exitCall = true;
        } else {
            System.out.println("Game over!\n");
            m_board.display();
        }
    }

}
