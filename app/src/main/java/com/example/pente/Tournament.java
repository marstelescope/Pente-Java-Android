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
import java.util.Random;

public class Tournament {
    private Board m_board;
    private Player[] m_roster;
    private int m_compScore;
    private int m_humanScore;

    public Tournament(Board a_board, Player[] a_roster) {
        m_board = a_board;
        m_roster = a_roster;
        m_compScore = 0;
        m_humanScore = 0;
    }

    /**
     Simulates coin toss to determine first to play
     @return index of first player
     */

    public int coinToss() {
        Console c = System.console();
        Random random = new Random();

        System.out.print("Coin toss to determine first move. Choose 'heads' or 'tails': ");
        String input = c.readLine();

        while (!input.equalsIgnoreCase("heads") && !input.equalsIgnoreCase("tails")){
            System.out.print("Please enter 'heads' or 'tails': ");
            input = c.readLine();
        }

        // Random value generated, converted immediately to heads or tails
        String winner = (random.nextInt(2) == 0 ? "Heads" : "Tails");
        System.out.println(winner + " won!");

        winner = winner.toLowerCase();

        // If guessed incorrectly, computer goes first
        m_board.setCompNext(!winner.equalsIgnoreCase(input));
        if (!winner.equalsIgnoreCase(input)) {
            // Index of the computer player
            System.out.println("Computer goes first!");
            return 1;
        } else {
            System.out.println("Human goes first!");
            return 0;
        }
    }

    /**
     Simulates tournament by storing tournament scores locally for board to keep
     track of round score and displaying scores at completion of rounds
     @param a_index - index of first player
     */
    public void run(int a_index) {
        Round round = new Round(m_board, m_roster);

        // Get current score from m_board and set it as tournament score
        // m_board score variables set to zero as they will track round score
        m_humanScore = m_board.getHumanScore();
        m_compScore = m_board.getCompScore();
        m_board.setCompScore(0);
        m_board.setHumanScore(0);

        Console c = System.console();
        String input;

        // Play rounds while human says yes or until exit call
        do {
            // If the board is empty, the first player is determined
            if (m_board.countNonEmpty() == 0) {
                if (m_compScore == m_humanScore) {
                    a_index = coinToss();
                } else if (m_compScore > m_humanScore) {
                    System.out.println("Computer plays first because the computer has the highest score.");
                    a_index = 1;
                } else {
                    System.out.println("Human plays first because the human has the highest score.");
                    a_index = 0;
                }

                // Roster, next player, and color updated
                m_roster[a_index].setColor('W');
                m_roster[a_index ^ 1].setColor('B');
                m_board.setCompNext(a_index == 1);
                m_board.setBlackNext(false);
            }

            // Round starts
            round.play(a_index);

            // If the user didn't call to exit, the round ends in a win/draw
            // and scores are updated and displayed
            if (!round.exitCall()) {
                m_humanScore += m_board.getHumanScore();
                m_compScore += m_board.getCompScore();
                System.out.println("Round score:");
                System.out.println("Human: " + m_board.getHumanScore());
                System.out.println("Computer: " + m_board.getCompScore());
                System.out.print("Winner of this round: ");
                if (m_board.getHumanScore() > m_board.getCompScore()) {
                    System.out.println("human player!");
                } else if (m_board.getHumanScore() < m_board.getCompScore()) {
                    System.out.println("computer player!");
                }

                System.out.println("\nTournament scores:");
                System.out.println("Human: " + m_humanScore);
                System.out.println("Computer: " + m_compScore + "\n");

                m_board.boardInit();

                System.out.print("Play another round? Enter 'Y' or 'N': ");
                input = c.readLine();

                while (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
                    System.out.print("Please enter 'Y' or 'N': ");
                    input = c.readLine();
                }

                System.out.println();
                if (input.equalsIgnoreCase("n")) {
                    break;
                }
            }
        } while (!round.exitCall());

        // If the user didn't call to exit prematurely, tournament scores and winner are displayed
        if (!round.exitCall()) {
            endTournament();
        }

        // Updated to be saved accurately into a file
        m_board.setHumanScore(m_humanScore);
        m_board.setCompScore(m_compScore);
    }

    /**
     End of tournament display of scores and winner
     */
    public void endTournament() {
        System.out.println("Tournament scores:");
        System.out.println("Human: " + m_humanScore);
        System.out.println("Computer: " + m_compScore + "\n");
        
        if (m_compScore > m_humanScore) {
            System.out.println("The winner of the tournament is the computer player!");
        } else if (m_compScore < m_humanScore) {
            System.out.println("The winner of the tournament is the human player!");
        } else {
            System.out.println("The tournament ends in a draw!");
        }
    }

    public static void main(String[] args){

    }
}
