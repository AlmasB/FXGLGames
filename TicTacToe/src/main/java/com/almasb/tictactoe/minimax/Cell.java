package com.almasb.tictactoe.minimax;

/**
 * The Cell class models each individual cell of the game board.
 *
 * Adapted from https://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
 */
public class Cell {  // save as Cell.java
    // package access
    Seed content; // content of this cell of type Seed.
    // take a value of Seed.EMPTY, Seed.CROSS, or Seed.NOUGHT
    int row, col; // row and column of this cell, not used in this program

    /** Constructor to initialize this cell */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        clear();  // clear content
    }

    /** Clear the cell content to EMPTY */
    public void clear() {
        content = Seed.EMPTY;
    }

    /** Paint itself */
    public void paint() {
        switch (content) {
            case CROSS:  System.out.print(" X "); break;
            case NOUGHT: System.out.print(" O "); break;
            case EMPTY:  System.out.print("   "); break;
        }
    }
}
