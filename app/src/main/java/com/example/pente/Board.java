/**
 ************************************************************
 * Name:  Mariya Popova                                     *
 * Project:  3 Pente Java/Android                           *
 * Class: CMPS 366-01 Organization of Programming Languages *
 * Date:  November 15, 2023                                 *
 ************************************************************
 */

package com.example.pente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Board {
    public static final char EMPTY = 'O';
    public static final int MAXROWCOL = 19;
    private char[][] m_board = new char [MAXROWCOL][MAXROWCOL];
    private int m_humanCaptured;
    private int m_compCaptured;
    private boolean m_stop;
    private int m_humanScore;
    private int m_compScore;
    private int m_fiveCount;
    private boolean m_compNext;
    private boolean m_blackNext;
    private boolean m_compWon;
    private boolean m_humanWon;
    private boolean m_checkOnly;

    Board(){
        boardInit();
        m_stop = false;
    }

    public boolean isCompNext(){ return m_compNext; }
    public boolean isBlackNext(){ return m_blackNext; }
    public boolean isStop() { return m_stop; }
    public void setCompNext(boolean a_compNext){m_compNext = a_compNext;}
    public void setBlackNext(boolean a_blackNext){ m_blackNext = a_blackNext; }
    public int getHumanScore() {return m_humanScore;}
    public int getCompScore() {return m_compScore;}
    public void setCompScore(int a_compScore){ m_compScore = a_compScore; }
    public void setHumanScore(int a_humanScore){ m_humanScore = a_humanScore; }
    public int getCompCap() { return m_compCaptured; }
    public int getHumanCap() {return m_humanCaptured; }
    void setCompCap(int a_compCap){ m_compCaptured = a_compCap; }
    void setHumanCap(int a_humanCap){ m_humanCaptured = a_humanCap; }
    public char getColorByPos(int a_row, int a_col) {
        return m_board[a_row][a_col];
    }
    public void setColorByPos(int a_row, int a_col, char a_color){
        m_board[a_row][a_col] = a_color;
    }
    public void setCheck(boolean a_checkOnly){ m_checkOnly = a_checkOnly; }

    public char getCompColor(){
        if (m_compNext){
            return m_blackNext ? 'B' : 'W';
        }
        else {
            return m_blackNext ? 'W' : 'B';
        }
    }
    public char getHumanColor(){
        if (m_compNext){
            return m_blackNext ? 'W' : 'B';
        }
        else {
            return m_blackNext ? 'B' : 'W';
        }
    }
    public String getCompColorName(){
        return (getCompColor() == 'B' ? "black" : "white");
    }
    public String getHumanColorName(){
        return (getHumanColor() == 'B' ? "black" : "white");
    }

    /**
     Converts row and column to position name
     @param row - int, row
     @param col - int, column
     @return position name in string form
     */
    public String convertRowCol(int row, int col) {
        int ascii = 'A';
        char c = (char)(col + ascii);
        return Character.toString(c) + (MAXROWCOL - row);
    }

    /**
     Initializes board and all board variables
     */
    public void boardInit(){
        m_humanCaptured = 0;
        m_humanScore = 0;
        m_compCaptured = 0;
        m_compScore = 0;
        m_fiveCount = 0;
        m_compNext = false;
        m_blackNext = false;
        m_compWon = false;
        m_humanWon = false;
        m_checkOnly = false;
        for (int i = 0; i < MAXROWCOL; i++){
            for (int j = 0; j < MAXROWCOL; j++){
                m_board[i][j] = EMPTY;
            }
        }
    }

    /**
     Loads file into board variables
     @param fileReader - BufferedReader( InputStreamReader(FileInputStream) ) where FIS is local file
     @return true if file loaded correctly, false otherwise
     */

    public boolean load(BufferedReader fileReader){
        String line;
        try {
            // Board header
            fileReader.readLine();

            // Reading board values
            for (int i = 0; i < MAXROWCOL; i++){
                line = fileReader.readLine();

                // Extra or missing characters in board line
                if (line.length() != MAXROWCOL){
                    System.out.println("Board error.");
                    return false;
                }

                // Saving board line into double array, character by character
                for (int j = 0; j < MAXROWCOL; j++){
                    char c = line.charAt(j);

                    // Unexpected characters encountered, exit
                    if (c != EMPTY && c != 'B' && c != 'W'){
                        System.out.println("Invalid board piece.");
                        return false;
                    }

                    // Value from file saved to double array
                    m_board[i][j] = c;
                }
            }

            // EMPTY line
            fileReader.readLine();

            // Human header
            fileReader.readLine();

            // Captured pairs
            line = fileReader.readLine();
            m_humanCaptured = Integer.parseInt(line.substring(16));
            if (m_humanCaptured >= 5){
                System.out.println("Human captures exceed winning criteria.");
                return false;
            }

            // Saving score amount into object variable
            line = fileReader.readLine();
            m_humanScore = Integer.parseInt(line.substring(7));

            // EMPTY line
            fileReader.readLine();

            // Computer header
            fileReader.readLine();

            // Captured pairs
            line = fileReader.readLine();
            m_compCaptured = Integer.parseInt(line.substring(16));
            if (m_compCaptured >= 5){
                System.out.println("Computer captures exceed winning criteria.");
                return false;
            }

            // Saving score amount into object variable
            line = fileReader.readLine();
            m_compScore = Integer.parseInt(line.substring(7));

            // EMPTY line
            fileReader.readLine();

            // Identifying next player by locating separator - syntax
            line = fileReader.readLine();
            int separator = line.indexOf(" - ");
            String player = line.substring(13, separator);
            String color = line.substring(separator + 3);

            // Checking validity
            if (!player.equals("Human") && !player.equals("Computer")){
                System.out.println("Player invalid.");
                return false;
            }
            if (!color.equals("White") && !color.equals("Black")){
                System.out.println("Color invalid.");
                return false;
            }

            // Saving values into object variables
            m_compNext = (player.equals("Computer"));
            m_blackNext = (color.equals("Black"));

        } catch (IOException e) {
            return false;
        }

        // Checking that there are no existing 5 in a rows in file, which
        // exceeds winning criteria, in which case a new game must be started
        return (findPattern("BBBBB").isEmpty() && findPattern("WWWWW").isEmpty());
    }

    /**
     Loads file into board variables
     @param a_filename - String, name of file in current directory
     @return true if file loaded correctly, false otherwise
     */
    // https://www.w3schools.com/java/java_files_read.asp
    public boolean load(String a_filename){
        File file = new File(a_filename);
        if(!file.exists()) {
            System.out.println("File not found.");
            return false;
        }

        try {
            Scanner fileReader = new Scanner(file);
            String line;

            // Board header
            fileReader.nextLine();

            // Reading board values
            for (int i = 0; i < MAXROWCOL; i++){
                line = fileReader.nextLine();

                // Extra or missing characters in board line
                if (line.length() != MAXROWCOL){
                    System.out.println("Board error.");
                    fileReader.close();
                    return false;
                }

                // Saving board line into double array, character by character
                for (int j = 0; j < MAXROWCOL; j++){
                    char c = line.charAt(j);

                    // Unexpected characters encountered, exit
                    if (c != EMPTY && c != 'B' && c != 'W'){
                        System.out.println("Invalid board piece.");
                        fileReader.close();
                        return false;
                    }

                    // Value from file saved to double array
                    m_board[i][j] = c;
                }
            }

            // EMPTY line
            fileReader.nextLine();

            // Human header
            fileReader.nextLine();

            // Captured pairs
            line = fileReader.nextLine();
            m_humanCaptured = Integer.parseInt(line.substring(16));
            if (m_humanCaptured >= 5){
                System.out.println("Human captures exceed winning criteria.");
                fileReader.close();
                return false;
            }

            // Saving score amount into object variable
            line = fileReader.nextLine();
            m_humanScore = Integer.parseInt(line.substring(7));

            // EMPTY line
            fileReader.nextLine();

            // Computer header
            fileReader.nextLine();

            // Captured pairs
            line = fileReader.nextLine();
            m_compCaptured = Integer.parseInt(line.substring(16));
            if (m_compCaptured >= 5){
                System.out.println("Computer captures exceed winning criteria.");
                fileReader.close();
                return false;
            }

            // Saving score amount into object variable
            line = fileReader.nextLine();
            m_compScore = Integer.parseInt(line.substring(7));

            // EMPTY line
            fileReader.nextLine();

            // Identifying next player by locating separator - syntax
            line = fileReader.nextLine();
            int separator = line.indexOf(" - ");
            String player = line.substring(13, separator);
            String color = line.substring(separator + 3);

            // Checking validity
            if (!player.equals("Human") && !player.equals("Computer")){
                System.out.println("Player invalid.");
                fileReader.close();
                return false;
            }
            if (!color.equals("White") && !color.equals("Black")){
                System.out.println("Color invalid.");
                fileReader.close();
                return false;
            }

            // Saving values into object variables
            m_compNext = (player.equals("Computer"));
            m_blackNext = (color.equals("Black"));

            // Closing file
            fileReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while loading file.");
            return false;
        }

        // Checking that there are no existing 5 in a rows in file, which
        // exceeds winning critera, in which case a new game must be started
        return (findPattern("BBBBB").isEmpty() && findPattern("WWWWW").isEmpty());
    }

    /**
     Saves current game state into file
     @param fileWriter - BufferedWriter, file location
     */
    public void save(BufferedWriter fileWriter){
        try {
            fileWriter.write("Board:\n");
            for (int i = 0; i < MAXROWCOL; i++){
                for (int j = 0; j < MAXROWCOL; j++){
                    fileWriter.write(m_board[i][j]);
                }
                fileWriter.write("\n");
            }
            fileWriter.write("\n");
            fileWriter.write("Human:\n");
            fileWriter.write("Captured pairs: " + m_humanCaptured + "\n");
            fileWriter.write("Score: " + m_humanScore + "\n\n");
            fileWriter.write("Computer:\n");
            fileWriter.write("Captured pairs: " + m_compCaptured + "\n");
            fileWriter.write("Score: " + m_compScore + "\n\n");
            fileWriter.write("Next Player: " + (m_compNext? "Computer":"Human") + " - "
                    + (m_blackNext? "Black":"White"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     Saves current game state into file
     @param a_filename - String, name of file in current directory
     */
    // https://www.w3schools.com/java/java_files_create.asp
    public void save(String a_filename){
        try {
            FileWriter myWriter = new FileWriter(a_filename);
            myWriter.write("Board:\n");
            for (int i = 0; i < MAXROWCOL; i++){
                for (int j = 0; j < MAXROWCOL; j++){
                    myWriter.write(m_board[i][j]);
                }
                myWriter.write("\n");
            }
            myWriter.write("\n");
            myWriter.write("Human:\n");
            myWriter.write("Captured pairs: " + m_humanCaptured + "\n");
            myWriter.write("Score: " + m_humanScore + "\n\n");
            myWriter.write("Computer:\n");
            myWriter.write("Captured pairs: " + m_compCaptured + "\n");
            myWriter.write("Score: " + m_compScore + "\n\n");
            myWriter.write("Next Player: " + (m_compNext? "Computer":"Human") + " - "
                    + (m_blackNext? "Black":"White"));

            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving.");
            e.printStackTrace();
        }
    }

    /**
     Validates input of move in string form and separates it into a_column and a_row
     @param a_input - String, move input in form "A19"
     @param a_column - char[], corresponding column if a_input is valid
     @param a_row - int[], corresponding row if a_input is valid
     @return true if valid input, false otherwise
     */
    private boolean validateInput(String a_input, char[] a_column, int[] a_row) {
        // Check length of input - shouldn't be less than 2 or more than 3
        if (a_input.length() < 2 || a_input.length() > 3) {
            return false;
        }

        // Check first character is between 'a' and 's', change a_column if so
        char inputColumn = Character.toLowerCase(a_input.charAt(0));
        if (inputColumn < 'a' || inputColumn > 's') {
            return false;
        }
        a_column[0] = inputColumn;

        // Check the second character to be a number
        if (Character.isDigit(a_input.charAt(1))) {
            // Convert the second and third character to an integer, changing a_row
            a_row[0] = Integer.parseInt(a_input.substring(1));
            // Check if the conversion is between 1 and MAXROWCOL (e.g., 19)
            if (a_row[0] >= 1 && a_row[0] <= MAXROWCOL) {
                return true;
            }
        }
        return false;
    }

    /**
     Displays board and number of captures for each player
     */
    public void display(){
        System.out.println("Board: ");
        for (int i = 0; i < MAXROWCOL; i++){
            System.out.print((MAXROWCOL - i) + " ");
            // For formatting purposes
            if (i > 9){
                System.out.print(" ");
            }
            // Display board row
            for (int j = 0; j < MAXROWCOL; j++){
                System.out.print(m_board[i][j]);
            }
            System.out.print("\n");
        }
        System.out.println("   ABCDEFGHIJKLMNOPQRS");
        System.out.println("Human:");
        System.out.println("Captured pairs: " + m_humanCaptured);
        System.out.println("Computer:");
        System.out.println("Captured pairs: " + m_compCaptured);
        System.out.println("");
    }

    /**
     Validates input of move in string form and adjusts row and column
     to corresponding indexes in board double array
     @param a_color - char, color of stone being placed
     @param a_move - String, move input in form "A19"
     @return empty string if move placed successfully, error encountered message otherwise
     */
    public String makeMove(char a_color, String a_move){
        char[] col = new char[1];
        int[] row = new int[1];

        if (!validateInput(a_move, col, row)) {
            return "Input invalid.";
        }

        int columnIndex = Character.toLowerCase(col[0]) - 'a';
        int rowIndex = MAXROWCOL - row[0];

        return makeMove(a_color, rowIndex, columnIndex);
    }

    /**
     Places the move on the board if it is allowed at this time,
     checks if move results in win or captures, updates next player
     @param a_color - char, color of stone being placed
     @param a_row - int, row of move
     @param a_column - int, column of move
     @return empty string if move placed successfully, error encountered message otherwise
     */
    public String makeMove(char a_color, int a_row, int a_column){
        // Check that row and column is EMPTY for stone to be placed
        if (m_board[a_row][a_column] == EMPTY){
            if (countNonEmpty() == 0){
                if (a_row != 9 || a_column != 9){
                    return "On the first turn, the first player must place a white stone at the center, J10.";
                }
            }
            if (countNonEmpty() == 2){
                if (a_row > 6 && a_row < 12 &&
                        a_column > 6 && a_column < 12){
                    // Remind restriction
                    return "On the second turn, the first player must place another white stone " +
                            "at least 3 intersections away from center.";
                }
            }

            // Place stone
            m_board[a_row][a_column] = a_color;

            // Check if this placement results in a capture or 5 in a row
            checkCapture(a_row, a_column);
            check5inRow(a_row, a_column);

            // Update next player, next color, and move count
            m_blackNext = !m_blackNext;
            m_compNext = !m_compNext;

            return "";
        }
        return "Board space taken.";
    }

    /**
     Counts number of non-empty spaces on the board
     @return number of non-empty spaces
     */
    public int countNonEmpty(){
        int count = 0;
        for (int i = 0; i < MAXROWCOL; i++){
            for (int j = 0; j < MAXROWCOL; j++){
                if (m_board[i][j] != EMPTY){
                    count++;
                }
            }
        }
        return count;
    }

    /**
     Checks if the current stone placement results in a capture and increment
     capture score for current player if so
     @param a_row - int, row of move
     @param a_column - int, column of move
     */
    public void checkCapture(int a_row, int a_column){
        // Storing current player's color and opponent's color
        char color = m_board[a_row][a_column];
        char opponentColor = (color == 'W' ? 'B' : 'W');
        int count  = 0;

        // Checking up the column
        if (a_row > 2 && m_board[a_row - 1][a_column] == opponentColor &&
                m_board[a_row - 2][a_column] == opponentColor &&
                m_board[a_row - 3][a_column] == color) {
            if (!m_checkOnly) {
                m_board[a_row - 1][a_column] = EMPTY;
                m_board[a_row - 2][a_column] = EMPTY;
            }
            count++;
        }
        // Checking down the column
        if (a_row < 16 && m_board[a_row + 1][a_column] == opponentColor &&
                m_board[a_row + 2][a_column] == opponentColor &&
                m_board[a_row + 3][a_column] == color) {
            if (!m_checkOnly) {
                m_board[a_row + 1][a_column] = EMPTY;
                m_board[a_row + 2][a_column] = EMPTY;
            }
            count++;
        }
        // Checking left in row
        if (a_column > 2 && m_board[a_row][a_column - 1] == opponentColor &&
                m_board[a_row][a_column - 2] == opponentColor &&
                m_board[a_row][a_column - 3] == color) {
            if (!m_checkOnly) {
                m_board[a_row][a_column - 1] = EMPTY;
                m_board[a_row][a_column - 2] = EMPTY;
            }
            count++;
        }
        // Checking right in row
        if (a_column < 16 && m_board[a_row][a_column + 1] == opponentColor &&
                m_board[a_row][a_column + 2] == opponentColor &&
                m_board[a_row][a_column + 3] == color) {
            if (!m_checkOnly) {
                m_board[a_row][a_column + 1] = EMPTY;
                m_board[a_row][a_column + 2] = EMPTY;
            }
            count++;
        }
        // Moving down right from up left
        if (a_column < 16 && a_row < 16 && m_board[a_row + 1][a_column + 1] == opponentColor &&
                m_board[a_row + 2][a_column + 2] == opponentColor &&
                m_board[a_row + 3][a_column + 3] == color) {
            if (!m_checkOnly) {
                m_board[a_row + 1][a_column + 1] = EMPTY;
                m_board[a_row + 2][a_column + 2] = EMPTY;
            }
            count++;
        }
        // Moving up left from down right
        if (a_column > 2 && a_row > 2 && m_board[a_row - 1][a_column - 1] == opponentColor &&
                m_board[a_row - 2][a_column - 2] == opponentColor &&
                m_board[a_row - 3][a_column - 3] == color) {
            if (!m_checkOnly) {
                m_board[a_row - 1][a_column - 1] = EMPTY;
                m_board[a_row - 2][a_column - 2] = EMPTY;
            }
            count++;
        }
        // Moving down left from up right
        if (a_column > 2 && a_row < 16 && m_board[a_row + 1][a_column - 1] == opponentColor &&
                m_board[a_row + 2][a_column - 2] == opponentColor &&
                m_board[a_row + 3][a_column - 3] == color) {
            if (!m_checkOnly) {
                m_board[a_row + 1][a_column - 1] = EMPTY;
                m_board[a_row + 2][a_column - 2] = EMPTY;
            }
            count++;
        }
        // Moving up right from down left
        if (a_column < 16 && a_row > 2 && m_board[a_row - 1][a_column + 1] == opponentColor &&
                m_board[a_row - 2][a_column + 2] == opponentColor &&
                m_board[a_row - 3][a_column + 3] == color) {
            if (!m_checkOnly) {
                m_board[a_row - 1][a_column + 1] = EMPTY;
                m_board[a_row - 2][a_column + 2] = EMPTY;
            }
            count++;
        }

        if (m_compNext) {
            m_compCaptured += count;
        } else {
            m_humanCaptured += count;
        }

    }

    /**
     Checks if the current stone placement results in a capture and increment
     capture score for current player if so
     @param a_row - int, row of stone placement
     @param a_column - int, column of stone placement
     @param a_row - int, row increment, direction being checked
     @param a_column - int, column increment, direction being checked
     @return true if 5 stones or more encountered, false otherwise
     */
    public boolean helper5Count(int a_row, int a_column, int a_incRow, int a_incCol) {
        int count = -1;
        char color = m_board[a_row][a_column];
        int row = a_row;
        int col = a_column;

        // Checking stones in one direction
        while (row >= 0 && col >= 0 && col < MAXROWCOL && row < MAXROWCOL && m_board[row][col] == color) {
            row += a_incRow;
            col += a_incCol;
            count++;
        }

        // Reset to start point
        row = a_row;
        col = a_column;

        // Checking stones in the other direction
        while (row >= 0 && col >= 0 && col < MAXROWCOL && row < MAXROWCOL && m_board[row][col] == color) {
            row -= a_incRow;
            col -= a_incCol;
            count++;
        }
        // 5 in a row found!
        return (count >= 5);
    }

    /**
     Count how many 5 in a rows the stone placement resulted in
     @param a_row - int, row of stone placement
     @param a_column - int, column of stone placement
     @return number of 5 in a rows achieved by current player
     */
    public int count5inRow(int a_row, int a_column) {
        int fiveInRow = 0;
        if (helper5Count(a_row, a_column, 0, 1)) {
            fiveInRow++;
        }
        if (helper5Count(a_row, a_column, 1, 0)) {
            fiveInRow++;
        }
        if (helper5Count(a_row, a_column, 1, 1)) {
            fiveInRow++;
        }
        if (helper5Count(a_row, a_column, 1, -1)) {
            fiveInRow++;
        }
        return fiveInRow;
    }

    /**
     Check if count5inRow returns a value greater than 0,
     and if so, update m_compWon or m_humanWon based on
     whose turn it is currently, and store the winner's count
     of 5 in a rows in m_fiveCount
     @param a_row - int, row of stone placement
     @param a_column - int, column of stone placement
     */
    public void check5inRow(int a_row, int a_column) {
        int fiveInRow = count5inRow(a_row, a_column);

        // If 5 in a row encountered, update m_compWon or m_humanWon based on whose turn it is currently
        if (fiveInRow > 0) {
            if (m_compNext) {
                m_compWon = true;
            } else {
                m_humanWon = true;
            }
        }
        m_fiveCount = fiveInRow;
    }

    /**
     Count number of 4 in a rows, checking one orientation at a time
     (up and down, left and right, UL to DR diagonal, DL to UR diagonal)
     @param a_color - color being checked
     @param a_row - starting row
     @param a_column - starting column
     @param a_incRow - row increment, direction being checked
     @param a_incCol - column increment, direction being checked
     @return number of 4 in a rows in given orientation
     */
    public int helper4Count(char a_color, int a_row, int a_column, int a_incRow, int a_incCol) {
        int count = 0;
        int count4 = 0;

        while (a_row >= 0 && a_column >= 0 && a_column < MAXROWCOL && a_row < MAXROWCOL) {
            // Color needed not encountered
            if (m_board[a_row][a_column] != a_color) {
                if (count == 4) {
                    count4++;
                }
                count = 0;
            } else {
                // Potential for 4 in a row
                count++;
            }
            a_row += a_incRow;
            a_column += a_incCol;
        }
        // Returns how many counts of 4 in the given row, column, or diagonal
        return count4;
    }

    /**
     Count total number of 4 in a rows for given color/player
     @param a_color - color being checked
     @return total number of 4 in a rows
     */
    public int count4inRow(char a_color) {
        int count = 0;
        for (int i = 0; i < MAXROWCOL; i++) {
            // Moving right
            count += helper4Count(a_color, i, 0, 0, 1);
            // Moving down
            count += helper4Count(a_color, 0, i, 1, 0);

            // Moving diag upper right to down left (upper half)
            if (i != 18) {
                count += helper4Count(a_color, 0, i, 1, -1);
            }
            // Moving diag upper right to down left (lower half)
            count += helper4Count(a_color, i, 18, 1, -1);

            // Moving diag upper left to down right (upper half)
            if (i != 0) {
                count += helper4Count(a_color, 0, i, 1, 1);
            }
            // Moving diag upper left to down right (lower half)
            count += helper4Count(a_color, i, 0, 1, 1);
        }
        return count;
    }

    /**
     Find the pattern passed and return vector of pattern's location
     @param a_pattern - pattern being checked, passed by reference, not altered
     @return list location of the pattern
     */
    public List<String> findPattern(String a_pattern) {
        List<String> pattern = new ArrayList<>();
        for (int i = 0; i < MAXROWCOL; i++) {
            // Checking right
            pattern = patternHelper(a_pattern, i, 0, 0, 1);
            if (!pattern.isEmpty()) {
                return pattern;
            }
            // Checking down
            pattern = patternHelper(a_pattern, 0, i, 1, 0);
            if (!pattern.isEmpty()) {
                return pattern;
            }

            // Checking diag upper right to down left (upper half)
            if (i != 18) {
                pattern = patternHelper(a_pattern, 0, i, 1, -1);
                if (!pattern.isEmpty()) {
                    return pattern;
                }
            }
            // Checking diag upper right to down left (lower half)
            pattern = patternHelper(a_pattern, i, 18, 1, -1);
            if (!pattern.isEmpty()) {
                return pattern;
            }

            // Checking diag upper left to down right (upper half)
            if (i != 0) {
                pattern = patternHelper(a_pattern, 0, i, 1, 1);
                if (!pattern.isEmpty()) {
                    return pattern;
                }
            }
            // Checking diag upper left to down right (lower half)
            pattern = patternHelper(a_pattern, i, 0, 1, 1);
            if (!pattern.isEmpty()) {
                return pattern;
            }
        }
        return pattern;
    }

    /**
     Helper function to find a_pattern passed, one board orientation
     at a time
     @param a_pattern - pattern being checked, passed by reference, not altered
     @param a_row - starting row
     @param a_column - starting column
     @param a_incRow - row increment, direction being checked
     @param a_incCol - column increment, direction being checked
     @return list location of the pattern
     */
    public List<String> patternHelper(String a_pattern, int a_row, int a_column, int a_incRow, int a_incCol) {
        List<String> pattern = new ArrayList<>();
        int row = a_row;
        int col = a_column;
        int count = 0;
        int restartRow = 0;
        int restartCol = 0;

        // Traverse board within bounds
        while (row >= 0 && col >= 0 && col < MAXROWCOL && row < MAXROWCOL) {
            // Check if intersection color corresponds with what is expected in the pattern
            if (m_board[row][col] != a_pattern.charAt(count)) {
                if (count > 0) {
                    count = 0;
                    // Restarting from the current pattern beginning (later incremented)
                    row = restartRow;
                    col = restartCol;
                    pattern.clear();
                }
            }
            else {
                if (count == 0) {
                    restartRow = row;
                    restartCol = col;
                }
                count++;
                pattern.add(convertRowCol(row, col));

                // Pattern found
                if (count == a_pattern.length()) {
                    return pattern;
                }
            }

            // Increment to the next position
            row += a_incRow;
            col += a_incCol;
        }

        // Pattern not found
        pattern.clear();
        return pattern;
    }


    /**
     Keep track of when game comes to end: full board, 5 in a row,
     or 5 or more captures
     @return string of condition that ended the game, empty string if game is not over
     */
    public String gameOver(){
        String ret = "";
        boolean gameOver = (countNonEmpty() == MAXROWCOL * MAXROWCOL);

        // Determine who won
        if (m_compWon) {
            ret = "Computer won by 5 in a row!";
            m_compScore += m_fiveCount * 5;

        } else if (m_humanWon) {
            ret = "Human won by 5 in a row!";
            m_humanScore += m_fiveCount * 5;
        } else if (gameOver) {
            ret = "It's a draw.";
        }
        if (m_compCaptured >= 5) {
            ret = "Computer won by 5 captures!";
        }
        if (m_humanCaptured >= 5) {
            ret = "Human won by 5 captures!";
        }

        // Add up total scores for the round
        if (!ret.isEmpty()) {
            m_humanScore += m_humanCaptured;
            m_compScore += m_compCaptured;
            m_humanScore += count4inRow(getHumanColor());
            m_compScore += count4inRow(getCompColor());
        }

        return ret;

    }
    public static void main(String[] args) {

    }
}
