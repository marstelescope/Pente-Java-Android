/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class Player {
    protected char m_color;
    protected Board m_board;

    Player(Board a_board){
        this.m_board = a_board;
        m_color = ' ';
    }

    char getColor() { return m_color; }

    char getOpponentColor() { return (m_color == 'W' ? 'B' : 'W'); }

    String getColorName() { return (m_color == 'W' ? "white" : "black"); }

    public void setColor(char a_color){
        m_color = a_color;
    }

    public String makeTurn(String move){
        return "";
    }

    /**
     Suggests a move for current player (computer always, human when
     help is requested) based on strategy in order of importance
     @return String of strategy with the move as the last word
     */
    protected String suggestMove() {
        String move;

        // First move restriction
        if (m_board.countNonEmpty() == 0){
            return "First move must be J10";
        }

        // Second move restriction
        if (m_board.countNonEmpty() == 2){
            String ret = "Second white stone must be 3" +
                    " intersections away from the center stone: ";
            if (m_board.getColorByPos(5,9) == Board.EMPTY){
                ret += m_board.convertRowCol(6,9);
            }
            else {
                ret += m_board.convertRowCol(12,9);
            }
            return ret;
        }

        // Find highest scoring winning move. If multiple winning moves and
        // opponent isn't one move away from winning, winning delayed, next >
        move = maxFindWinning(getColor());
        if (!move.isEmpty()) {
            return "Found winning move with highest score: " + move;
        }

        // Check if opponent can win and block this move
        move = maxFindWinning(getOpponentColor());
        if (!move.isEmpty()) {
            return "Block winning move: " + move;
        }

        // Check if opponent can win in two moves, and block one of them
        move = blockWinIn2();
        if (!move.isEmpty()) {
            return "Block winning in two moves: " + move;
        }

        // Check if opponent can be captured, find highest scoring
        move = maxCapture(getColor());
        if (!move.isEmpty()) {
            return "Capture opponent: " + move;
        }

        // Check if the current player can avoid being captured
        move = maxCapture(getOpponentColor());
        if (!move.isEmpty()) {
            return "Avoid being captured: " + move;
        }

        // Add 4th stone to an existing chain of 3
        move = find3();
        if (!move.isEmpty()) {
            return "Create a row of 4 stones: " + move;
        }

        // Add 3rd stone to an existing chain of 2
        move = find2();
        if (!move.isEmpty()) {
            return "Create a row of 3 stones: " + move;
        }

        // Add a stone near an existing stone
        move = find1(getColor());
        if (!move.isEmpty()) {
            return "Add a stone near an existing cluster: " + move;
        }

        // Add a stone near opponent
        move = find1(getOpponentColor());
        if (!move.isEmpty()) {
            return "Add a stone near opponent: " + move;
        }

        // If all else fails
        return "First available slot: " + findAvailable();
    }

    /**
     Find highest scoring winning move
     @return String of move, empty if none found
     */
    public String maxFindWinning(char a_color) {
        int countWinPos = 0;
        Map<Integer, List<Pair<Integer>>> maxScore = new HashMap<>();

        // Scan the board for places where moves can be placed
        for (int i = 0; i < Board.MAXROWCOL; i++) {
            for (int j = 0; j < Board.MAXROWCOL; j++) {
                if (m_board.getColorByPos(i, j) == Board.EMPTY) {
                    // Temporary stone placement
                    m_board.setColorByPos(i, j, a_color);

                    // Record the score that placement results in if > 0
                    int count = m_board.count5inRow(i, j);
                    if (count > 0) {
                        Pair<Integer> pair = new Pair<Integer>();
                        pair.setValue(i,j);
                        if (!maxScore.containsKey(count)) {
                            maxScore.put(count, new ArrayList<>());
                        }
                        maxScore.get(count).add(pair);

                        countWinPos++;
                    }

                    // Restore the board position
                    m_board.setColorByPos(i, j, Board.EMPTY);
                }
            }
        }

        if (!maxScore.isEmpty()) {
            // Placing the move on the most scoring spot
            List<Pair<Integer>> maxScoreList = maxScore.get(Collections.max(maxScore.keySet()));
            Pair<Integer> move = maxScoreList.get(0);
            return m_board.convertRowCol(move.getFirst(), move.getSecond());
        }

        // No winning position
        return "";
    }

    /**
     Find clusters that could win in two moves and block
     @return String of move, empty if none found
     */
    public String blockWinIn2() {
        List<String> patternLoc = new ArrayList<>();
        char[] pat = new char[6];
        pat[0] = Board.EMPTY;
        pat[5] = Board.EMPTY;

        // Shifting the pattern we are searching for
        // i.e. __CCC_ could win in 2, _C_CC_, etc
        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                pat[j] = getOpponentColor();
            }
            pat[i] = Board.EMPTY;
            patternLoc = m_board.findPattern(new String(pat));

            // Block win in 2 found
            if (!patternLoc.isEmpty()) {
                return patternLoc.get(i);
            }
        }

        // No win in 2 patterns
        return "";
    }

    /**
     Find most scoring capture, if any available
     @return String of move, empty if none found
     */
    public String maxCapture(char a_color) {
        int countWinPos = 0;
        Map<Integer, List<Pair<Integer>>> maxScore = new HashMap<>();

        // In order for values not to be altered
        int compCaptured = m_board.getCompCap();
        int humanCaptured = m_board.getHumanCap();

        // Scan the board for places where moves can be placed
        for (int i = 0; i < Board.MAXROWCOL; i++) {
            for (int j = 0; j < Board.MAXROWCOL; j++) {
                if (m_board.getColorByPos(i, j) == Board.EMPTY) {
                    // Temporarily stone placement
                    m_board.setColorByPos(i, j, a_color);
                    m_board.setCheck(true);
                    m_board.checkCapture(i, j);
                    Pair<Integer> pair = new Pair<>();
                    pair.setValue(i,j);

                    // If placement resulted in a score change, store the score
                    if (compCaptured != m_board.getCompCap()) {
                        if (!maxScore.containsKey(m_board.getCompCap() - compCaptured)) {
                            maxScore.put(m_board.getCompCap() - compCaptured, new ArrayList<>());
                        }
                        maxScore.get(m_board.getCompCap() - compCaptured).add(pair);
                        m_board.setCompCap(compCaptured);
                    }
                    if (humanCaptured != m_board.getHumanCap()) {
                        if (!maxScore.containsKey(m_board.getHumanCap() - humanCaptured)) {
                            maxScore.put(m_board.getHumanCap() - humanCaptured, new ArrayList<>());
                        }
                        maxScore.get(m_board.getHumanCap() - humanCaptured).add(pair);
                        m_board.setHumanCap(humanCaptured);
                    }

                    // Restore values
                    m_board.setColorByPos(i, j, Board.EMPTY);
                    m_board.setCheck(false);
                }
            }
        }
        if (!maxScore.isEmpty()) {
            // Placing the move on the most scoring spot
            List<Pair<Integer>> maxScoreList = maxScore.get(Collections.max(maxScore.keySet()));
            Pair<Integer> move = maxScoreList.get(0);
            return m_board.convertRowCol(move.getFirst(), move.getSecond());
        }

        // No winning position
        return "";
    }

    /**
     Find clusters of 3 stones that can turn to 4
     @return String of move, empty if none found
     */
    public String find3() {
        List<String> patternLoc = new ArrayList<>();
        char[] pat = new char[5];

        // Pattern *CCC_ (star is returning value)
        pat[0] = pat[4] = Board.EMPTY;
        pat[1] = pat[2] = pat[3] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(0);
        }

        // Pattern _*CCC (star is returning value)
        pat[1] = Board.EMPTY;
        pat[4] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(1);
        }

        // Pattern CCC*_ (star is returning value)
        pat[0] = pat[1] = getColor();
        pat[3] = pat[4] = Board.EMPTY;
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(3);
        }

        // Pattern C*CC_ (star is returning value)
        pat[1] = Board.EMPTY;
        pat[3] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(1);
        }

        // Pattern _C*CC (star is returning value)
        pat[0] = pat[2] = Board.EMPTY;
        pat[1] = pat[4] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(2);
        }

        // Pattern CC*C_ (star is returning value)
        pat[0] = getColor();
        pat[4] = Board.EMPTY;
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(2);
        }

        // Pattern _CC*C (star is returning value)
        pat[0] = pat[3] = Board.EMPTY;
        pat[2] = pat[4] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(3);
        }

        return "";
    }

    /**
     Find clusters of 2 stones that can turn to 3
     @return String of move, empty if none found
     */
    public String find2() {
        List<String> patternLoc = new ArrayList<>();
        char[] pat = new char[4];

        // Pattern *CC_ (star is returning value)
        pat[0] = pat[3] = Board.EMPTY;
        pat[1] = pat[2] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(0);
        }

        // Pattern _*CC (star is returning value)
        pat[1] = Board.EMPTY;
        pat[3] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(1);
        }

        // Pattern CC*_ (star is returning value)
        pat[0] = pat[1] = getColor();
        pat[3] = pat[2] = Board.EMPTY;
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(2);
        }

        // Pattern C*C_ (star is returning value)
        pat[1] = Board.EMPTY;
        pat[2] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(1);
        }

        // Pattern _C*C (star is returning value)
        pat[0] = pat[2] = Board.EMPTY;
        pat[1] = pat[3] = getColor();
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(2);
        }

        return "";
    }

    /**
     Find single stones near which another stone can be placed
     while also avoiding being captured
     @param a_color - char, color of stones being searches for
     @return String of move, empty if none found
     */
    public String find1(char a_color) {
        List<String> patternLoc;
        char[] pat = new char[4];
        char opponentColor = (a_color == 'W' ? 'B' : 'W');

        // Pattern _C*_ (star is returning value)
        pat[1] = a_color;
        pat[0] = pat[3] = pat[2] = Board.EMPTY;
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(2);
        }

        // Pattern _*C_ (star is returning value)
        pat[2] = a_color;
        pat[1] = Board.EMPTY;
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            return patternLoc.get(1);
        }

        // Pattern *_CO (star is returning value)
        pat[3] = opponentColor;
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            // Avoid capture
            return patternLoc.get(0);
        }

        // Pattern OC_* (star is returning value)
        pat[0] = opponentColor;
        pat[1] = a_color;
        pat[3] = pat[2] = Board.EMPTY;
        patternLoc = m_board.findPattern(new String(pat));
        if (!patternLoc.isEmpty()) {
            // Avoid capture
            return patternLoc.get(3);
        }

        return "";
    }

    /**
     Find first available move in the case there are no patterns
     @return String of move, empty if none found
     */
    public String findAvailable() {
        for (int i = 0; i < Board.MAXROWCOL; i++) {
            for (int j = 0; j < Board.MAXROWCOL; j++) {
                if (m_board.getColorByPos(i, j) == Board.EMPTY) {
                    return m_board.convertRowCol(i, j);
                }
            }
        }
        return "";
    }

    public static void main(String[] args){

    }

}
